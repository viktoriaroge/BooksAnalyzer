package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverCandidatesUseCase
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverHeadersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoverPickerViewModel @Inject constructor(
    private val getCoverCandidates: GetBookCoverCandidatesUseCase,
    private val getCoverHeaders: GetBookCoverHeadersUseCase,
    private val mapper: BookCoverMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(BookCoverPickerUiState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(initialized = false) }
    }

    fun openCoverPicker(book: Book) {
        if (_state.value.initialized) {
            _state.update { it.copy(isOpen = true) }
            return
        }

        _state.update { it.copy(isOpen = true, isLoading = true) }

        val selected = BookCoverState(
            url = book.coverUrl ?: "",
            headers = book.coverRequestHeaders,
        )

        viewModelScope.launch {
            _state.update {
                it.copy(
                    initialized = true,
                    isLoading = false,
                    screenValues = mapper.getStaticScreenValues(),
                    selectedCover = selected,
                    bookCovers = getCoverCandidates(book)
                        .map { candidate ->
                            mapper.map(candidate = candidate)
                        },
                )
            }
        }
    }

    fun closeCoverPicker() {
        _state.update { it.copy(isOpen = false) }
    }

    fun onManualUrlChange(newUrl: String) {
        _state.update { it.copy(manualUrlInput = newUrl) }
    }

    fun addManualUrl() {
        val url = _state.value.manualUrlInput.trim()
        if (url.isEmpty()) return

        val currentCandidates = _state.value.bookCovers
        if (currentCandidates.any { it.url == url }) {
            _state.update { it.copy(manualUrlInput = "") }
            return
        }

        val newCandidate = BookCoverState(
            url = url,
            headers = getCoverHeaders(url),
        )
        _state.update {
            it.copy(
                bookCovers = listOf(newCandidate) + _state.value.bookCovers,
                manualUrlInput = "",
            )
        }
    }

    fun selectCover(url: String) {
        _state.update {
            it.copy(
                selectedCover = BookCoverState(
                    url = url,
                    headers = getCoverHeaders(url),
                ),
            )
        }
        closeCoverPicker()
    }
}
