package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import javax.inject.Inject

class ConfirmBookMapper @Inject constructor() {

    fun getScreenValues(): ConfirmBookScreenValues = ConfirmBookScreenValues(
        screenTitleConfirm = R.string.confirm_book_screen_name,
        screenTitleManual = R.string.confirm_book_screen_in_manual_mode_name,
        changeCoverButtonLabel = R.string.confirm_book_screen_change_book_cover_button_label,
        isbnLabel = R.string.confirm_book_screen_isbn13_label,
        sourceLabel = R.string.confirm_book_screen_source_label,
        saveButtonLabel = R.string.confirm_book_screen_save_button_label,

        manualInstruction = R.string.confirm_book_screen_manual_form_instruction_text,
        manualTitleLabel = R.string.confirm_book_screen_manual_form_title_label,
        manualAuthorLabel = R.string.confirm_book_screen_manual_form_author_label,
        manualYearLabel = R.string.confirm_book_screen_manual_form_year_label,
        manualIsbn13Label = R.string.confirm_book_screen_manual_form_isbn13_label,
        manualCoverUrlLabel = R.string.confirm_book_screen_manual_form_cover_url_label,
        manualSaveButtonLabel = R.string.confirm_book_screen_manual_form_save_button_label,
    )

    fun mapToDataState(
        book: Book,
        selectedCoverUrl: String?,
        headersForBookCover: Map<String, String>?
    ): ConfirmBookDataState = ConfirmBookDataState(
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        isbn13 = book.isbn13,
        coverUrl = selectedCoverUrl ?: book.coverUrl,
        coverHeaders = headersForBookCover ?: book.coverRequestHeaders,
        sourceBadgeTextRes = when (book.source) {
            BookSource.GOOGLE_BOOKS -> R.string.book_source_full_google_books
            BookSource.OPEN_LIBRARY -> R.string.book_source_full_open_library
            BookSource.MANUAL -> R.string.book_source_full_added_manually
        },
    )

    fun mapToManualFormData(
        prefillQuery: String?,
        prefillMode: SearchMode?,
    ): ConfirmBookManualFormData {
        val query = prefillQuery ?: ""
        val mode = prefillMode ?: SearchMode.ALL

        return ConfirmBookManualFormData(
            initialTitle = if (mode == SearchMode.ALL || mode == SearchMode.TITLE) query else "",
            initialAuthors = if (mode == SearchMode.AUTHOR) query else "",
            initialIsbn13 = if (mode == SearchMode.ISBN) query else "",
        )
    }
}
