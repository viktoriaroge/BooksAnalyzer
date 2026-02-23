package com.viroge.booksanalyzer.ui.components.bookcover

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

    var pickerAlreadyLoaded: Boolean = false

    init {
        Log.d("CoverPickerViewModel", "init")
        _coverPicker.value = CoverPickerUiState()
        _selectedCover.value = CoverData()
        pickerAlreadyLoaded = false
    }

    fun openCoverPicker(book: Book) {
        if (pickerAlreadyLoaded) {
            _coverPicker.value = _coverPicker.value.copy(isOpen = true)
            return
        }

        _coverPicker.value = _coverPicker.value.copy(isOpen = true, isLoading = true)

        if (!_selectedCover.value.isSelected) {
            val preselectCover = book.coverUrl != null
            _selectedCover.value = CoverData(
                url = book.coverUrl ?: "",
                headers = book.coverRequestHeaders,
                isSelected = preselectCover,
            )
        }

        viewModelScope.launch {
            val candidates = CoverUrlOptimizer.getCoverCandidates(book = book)
            _coverPicker.value = CoverPickerUiState(
                isOpen = true,
                isLoading = false,
                candidates = candidates,
            )
        }
        pickerAlreadyLoaded = true
    }

    fun closeCoverPicker() {
        _coverPicker.value = _coverPicker.value.copy(isOpen = false)
    }

    fun onManualUrlChange(newUrl: String) {
        _coverPicker.value = _coverPicker.value.copy(manualUrlInput = newUrl)
    }

    fun addManualUrl() {
        val url = _coverPicker.value.manualUrlInput.trim()
        if (url.isEmpty()) return

        val headers = CoverUrlOptimizer.getCoverHeaders(url)
        val newCandidate = url to headers
        _coverPicker.value = _coverPicker.value.copy(
            candidates = _coverPicker.value.candidates + newCandidate,
            manualUrlInput = "",
        )
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
    val manualUrlInput: String = "",
    val candidates: List<Pair<String, Map<String, String>>> = emptyList(),
)
