package com.viroge.booksanalyzer.ui.screens.books.confirm

sealed interface ConfirmEvent {

    data object Saved : ConfirmEvent

    data class Error(
        val message: String,
    ) : ConfirmEvent
}
