package com.viroge.booksanalyzer.ui.screens.books.library.collection

import com.viroge.booksanalyzer.domain.model.BookSeed

sealed interface CollectionEvent {
    data class OpenBookDetails(val seed: BookSeed) : CollectionEvent
}
