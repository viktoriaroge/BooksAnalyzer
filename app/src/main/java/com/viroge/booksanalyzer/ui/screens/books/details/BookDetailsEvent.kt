package com.viroge.booksanalyzer.ui.screens.books.details

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed interface BookDetailsEvent {
    data class Error(
        val errorType: DetailsErrorType,
    ) : BookDetailsEvent
}

enum class DetailsErrorType(val message: UiText) {
    LOADING_BOOK_FAILED(UiText.StringResource(R.string.book_details_screen_error_book_loading)),
    UPDATING_STATUS_FAILED(UiText.StringResource(R.string.book_details_screen_error_status_update)),
    SAVING_FAILED(UiText.StringResource(R.string.book_details_screen_error_saving)),
}
