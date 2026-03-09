package com.viroge.booksanalyzer.ui.screens.books.details

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
import javax.inject.Inject

class BookDetailsMapper @Inject constructor() {

    fun getScreenValues(isInEditMode: Boolean): BookDetailsScreenValues = BookDetailsScreenValues(
        screenName =
            if (isInEditMode) R.string.book_details_screen_in_edit_screen_name
            else R.string.book_details_screen_name,
        originLabel = R.string.book_details_screen_source_label,
        deleteButtonText = R.string.book_details_screen_delete_default_button,
        changeCoverButtonText = R.string.book_details_screen_in_edit_change_book_cover_button_label,
        titleLabel = R.string.book_details_screen_in_edit_title_label,
        authorLabel = R.string.book_details_screen_in_edit_author_label,
        authorHint = R.string.book_details_screen_in_edit_author_hint,
        yearLabel = R.string.book_details_screen_in_edit_year_label,
        yearHint = R.string.book_details_screen_in_edit_year_hint,
        isbn13Label = R.string.book_details_screen_in_edit_isbn13_label,
        isbn10Label = R.string.book_details_screen_in_edit_isbn10_label,
        saveChangesButtonText = R.string.book_details_screen_in_edit_save_default_button,
        saveChangesInProgressButtonText = R.string.book_details_screen_in_edit_save_in_progress_button,
        cancelChangesButtonText = R.string.book_details_screen_in_edit_cancel_button,
    )

    fun getDeleteDialogValues(): BookDetailsDeleteDialogValues = BookDetailsDeleteDialogValues(
        title = R.string.book_details_screen_delete_book_dialog_title,
        message = UiText.StringResource(
            resId = R.string.book_details_screen_delete_book_dialog_text,
            UiText.StringResource(R.string.recently_deleted_screen_name),
            UiText.StringResource(R.string.settings_screen_name)
        ),
        deleteButtonText = R.string.book_details_screen_delete_book_dialog_delete_button,
        cancelButtonText = R.string.book_details_screen_delete_book_dialog_cancel_button,
    )

    fun getErrorState(errorType: BookDetailsErrorType): BookDetailsErrorState = BookDetailsErrorState(
        showError = errorType != BookDetailsErrorType.NONE,
        errorMessage = when (errorType) {
            BookDetailsErrorType.NONE -> R.string.empty_text
            BookDetailsErrorType.SAVING_FAILED -> R.string.book_details_screen_error_saving
            BookDetailsErrorType.TITLE_REQUIRED -> R.string.book_details_screen_error_title_required
            BookDetailsErrorType.LOADING_BOOK_FAILED -> R.string.book_details_screen_error_book_loading
            BookDetailsErrorType.UPDATING_STATUS_FAILED -> R.string.book_details_screen_error_status_update
        }
    )
}
