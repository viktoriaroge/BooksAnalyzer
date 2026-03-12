package com.viroge.booksanalyzer.ui.screens.books.search

import com.viroge.booksanalyzer.domain.model.Book

sealed interface SearchUiState {

    data object Idle : SearchUiState
    data object Loading : SearchUiState

    data class Success(
        val query: String,
        val items: List<Book>,
    ) : SearchUiState

    data class Partial(
        val query: String,
        val items: List<Book>,
        val messages: List<String>,
    ) : SearchUiState

    data class Empty(
        val query: String,
    ) : SearchUiState

    data class Error(
        val message: String,
    ) : SearchUiState
}
