package com.viroge.booksanalyzer.ui.screens.books.confirm

sealed interface ConfirmEvent {

    data class Saved(
        val bookId: String,
    ) : ConfirmEvent

    data class Error(
        val message: String,
    ) : ConfirmEvent
}
