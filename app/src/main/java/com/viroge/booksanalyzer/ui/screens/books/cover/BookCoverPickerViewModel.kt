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

    fun openCoverPicker(book: Book) {
        _state.update { it.copy(isOpen = true, isLoading = true) }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    initialized = true,
                    isLoading = false,
                    screenValues = mapper.getStaticScreenValues(),
                    bookCovers = getCoverCandidates(book)
                        .map { candidate ->
                            mapper.map(candidate = candidate)
                        },
                )
            }
        }

        val currentSelection = _state.value.selectedCover
        if (currentSelection.url == null && book.coverUrl != null) {
            _state.update {
                it.copy(
                    selectedCover = BookCoverState(
                        url = book.coverUrl,
                        headers = book.coverRequestHeaders,
                    )
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

        if (_state.value.bookCovers.any { it.url == url }
            || _state.value.manualBookCovers.any { it.url == url }) {
            _state.update { it.copy(manualUrlInput = "") }
            return
        }

        val newCandidate = BookCoverState(
            url = url,
            headers = getCoverHeaders(url),
        )
        _state.update {
            it.copy(
                manualBookCovers = listOf(newCandidate) + it.manualBookCovers,
                manualUrlInput = "",
                selectedCover = newCandidate,
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
