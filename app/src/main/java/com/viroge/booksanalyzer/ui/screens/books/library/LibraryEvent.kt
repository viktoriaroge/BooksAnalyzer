package com.viroge.booksanalyzer.ui.screens.books.library

sealed interface LibraryEvent {
    data object OpenBook : LibraryEvent
}
