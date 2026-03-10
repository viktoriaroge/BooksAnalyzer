package com.viroge.booksanalyzer.ui.screens.books.details

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
import com.viroge.booksanalyzer.ui.common.util.UiText
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import javax.inject.Inject

class BookDetailsMapper @Inject constructor() {

    fun getScreenValues(): BookDetailsScreenValues = BookDetailsScreenValues(
        screenName = R.string.book_details_screen_name,
        originLabel = R.string.book_details_screen_source_label,
        deleteButtonText = R.string.book_details_screen_delete_default_button,
    )

    fun getEditScreenValues(): BookDetailsEditScreenValues = BookDetailsEditScreenValues(
        screenName = R.string.book_details_screen_in_edit_screen_name,
        originLabel = R.string.book_details_screen_source_label,
        deleteButtonText = R.string.book_details_screen_delete_default_button,
        changeCoverButtonText = R.string.book_details_screen_in_edit_change_book_cover_button_label,
        titleLabel = R.string.book_details_screen_in_edit_title_label,
        titleError = R.string.book_details_screen_in_edit_title_error,
        authorLabel = R.string.book_details_screen_in_edit_author_label,
        authorError = R.string.book_details_screen_in_edit_author_error,
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

    fun mapToDataState(
        book: Book,
        selectedCandidate: BookCoverCandidate?,
    ): BookDetailsDataState = BookDetailsDataState(
        id = book.id,
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.publishedYear,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        url = selectedCandidate?.url ?: book.coverUrl,
        headers = selectedCandidate?.headers ?: book.coverRequestHeaders,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
