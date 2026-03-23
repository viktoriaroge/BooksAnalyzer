package com.viroge.booksanalyzer.ui.screens.books.library

import com.viroge.booksanalyzer.domain.model.BookSeed

sealed interface LibraryEvent {
    data class OpenBookDetails(val seed: BookSeed) : LibraryEvent
}
