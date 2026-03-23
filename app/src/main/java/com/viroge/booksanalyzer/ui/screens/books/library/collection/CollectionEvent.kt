package com.viroge.booksanalyzer.ui.screens.books.library.collection

sealed interface CollectionEvent {
    data object OpenBook : CollectionEvent
}
