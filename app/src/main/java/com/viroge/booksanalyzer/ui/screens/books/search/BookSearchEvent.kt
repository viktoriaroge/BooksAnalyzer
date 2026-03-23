package com.viroge.booksanalyzer.ui.screens.books.search

import com.viroge.booksanalyzer.domain.model.TempBook

sealed interface BookSearchEvent {
    data class OpenBookConfirmation(val seed: TempBook) : BookSearchEvent
}
