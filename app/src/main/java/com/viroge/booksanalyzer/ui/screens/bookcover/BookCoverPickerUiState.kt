package com.viroge.booksanalyzer.ui.screens.bookcover

data class BookCoverPickerUiState(
    val initialized: Boolean = false,
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,
    val manualUrlInput: String = "",
    val selectedCover: BookCoverState = BookCoverState(),
    val bookCovers: List<BookCoverState> = emptyList(),
)

data class BookCoverState(
    val url: String = "",
    val headers: Map<String, String> = emptyMap(),
)
