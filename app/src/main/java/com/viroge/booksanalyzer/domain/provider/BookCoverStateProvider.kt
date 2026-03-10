package com.viroge.booksanalyzer.domain.provider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverPickerStateProvider @Inject constructor() {

    private val _state = MutableStateFlow(BookCoverState())
    val state = _state.asStateFlow()

    fun getSelected(): BookCoverCandidate? = _state.value.selectedCandidate
    fun getBookCoverCandidates(): List<BookCoverCandidate> = _state.value.bookCovers
    fun getManualBookCoverCandidates(): List<BookCoverCandidate> = _state.value.manualBookCovers

    fun selectCover(
        url: String?,
        headers: Map<String, String>,
        isManualInput: Boolean = false,
    ) {
        when {
            isManualInput && url != null ->
                _state.update {
                    it.copy(
                        selectedCandidate = BookCoverCandidate(url, headers),
                        manualBookCovers = listOf(BookCoverCandidate(url, headers)) + it.manualBookCovers,
                    )
                }

            url != null ->
                _state.update { it.copy(selectedCandidate = BookCoverCandidate(url, headers)) }

            else -> _state.update { it.copy(selectedCandidate = null) }
        }
    }

    fun updateBookCovers(
        bookCovers: List<BookCoverCandidate>,
    ) {
        _state.update { it.copy(bookCovers = bookCovers) }
    }

    fun clear() {
        _state.value = BookCoverState()
    }
}

data class BookCoverState(
    val selectedCandidate: BookCoverCandidate? = null,
    val bookCovers: List<BookCoverCandidate> = emptyList(),
    val manualBookCovers: List<BookCoverCandidate> = emptyList(),
)

data class BookCoverCandidate(
    val url: String,
    val headers: Map<String, String>,
)
