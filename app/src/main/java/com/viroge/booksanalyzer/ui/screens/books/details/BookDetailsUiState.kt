package com.viroge.booksanalyzer.ui.screens.books.details

import com.viroge.booksanalyzer.domain.model.Book

data class BookDetailsUiState(
    val book: Book? = null,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val editTitle: String = "",
    val editAuthors: String = "",
    val editPublishedYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
)
