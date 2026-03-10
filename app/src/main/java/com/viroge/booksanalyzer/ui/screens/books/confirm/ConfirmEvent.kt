package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed interface ConfirmEvent {

    data object Saved : ConfirmEvent

    data class Error(
        val errorType: ConfirmErrorType,
    ) : ConfirmEvent
}

enum class ConfirmErrorType(val message: UiText) {
    SAVING_FAILED(UiText.StringResource(R.string.confirm_book_screen_error_saving)),
}
