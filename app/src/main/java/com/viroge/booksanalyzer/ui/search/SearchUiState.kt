package com.viroge.booksanalyzer.ui.search

import com.viroge.booksanalyzer.domain.BookCandidate

sealed interface SearchUiState {

    data object Idle : SearchUiState
    data object Loading : SearchUiState

    data class Success(
        val items: List<BookCandidate>,
    ) : SearchUiState

    data class Partial(
        val items: List<BookCandidate>,
        val messages: List<String>,
    ) : SearchUiState

    data class Empty(
        val query: String,
    ) : SearchUiState

    data class Error(
        val message: String,
    ) : SearchUiState
}
