package com.viroge.booksanalyzer.ui.books.cover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.CoverUrlOptimizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoverPickerViewModel @Inject constructor() : ViewModel() {

    private val _coverPicker = MutableStateFlow(CoverPickerUiState())
    val coverPicker = _coverPicker.asStateFlow()

    private val _selectedCover = MutableStateFlow(CoverData())
    val selectedCover = _selectedCover.asStateFlow()

    init {
        Log.d("CoverPickerViewModel", "init")
        _coverPicker.value = CoverPickerUiState()
        _selectedCover.value = CoverData()
    }

    fun openCoverPicker(book: Book) {
        if (!_selectedCover.value.isSelected) {
            val preselectCover = book.coverUrl != null
            _selectedCover.value = CoverData(
                url = book.coverUrl ?: "",
                headers = book.coverRequestHeaders,
                isSelected = preselectCover,
            )
        }

        _coverPicker.value = _coverPicker.value.copy(isOpen = true, isLoading = true)

        viewModelScope.launch {
            val candidates = CoverUrlOptimizer.getCoverCandidates(book = book)
            _coverPicker.value = CoverPickerUiState(
                isOpen = true,
                isLoading = false,
                candidates = candidates,
            )
        }
    }

    fun closeCoverPicker() {
        _coverPicker.value = _coverPicker.value.copy(isOpen = false)
    }

    fun selectCover(url: String) {
        _selectedCover.value = CoverData(
            url = url,
            headers = CoverUrlOptimizer.getCoverHeaders(url),
            isSelected = true,
        )
        closeCoverPicker()
    }
}

data class CoverData(
    val url: String = "",
    val headers: Map<String, String> = emptyMap(),
    val isSelected: Boolean = false,
)

data class CoverPickerUiState(
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,
    val candidates: List<Pair<String, Map<String, String>>> = emptyList(),
)
