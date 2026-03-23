package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed interface ConfirmBookEvent {

    data object Saved : ConfirmBookEvent

    data class Error(
        val errorType: ConfirmErrorType,
    ) : ConfirmBookEvent

    data object OpenBookCoverPicker : ConfirmBookEvent
}

enum class ConfirmErrorType(val message: UiText) {
    SAVING_FAILED(UiText.StringResource(R.string.confirm_book_screen_error_saving)),
}
