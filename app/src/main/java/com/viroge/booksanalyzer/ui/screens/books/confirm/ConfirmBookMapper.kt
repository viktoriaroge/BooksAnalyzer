package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
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
        selectedCandidate: BookCoverCandidate?,
    ): ConfirmBookDataState = ConfirmBookDataState(
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        isbn13 = book.isbn13,
        source = BookSourceUi.fromDomain(book.source),
        url = selectedCandidate?.url ?: book.coverUrl,
        headers = selectedCandidate?.headers ?: book.coverRequestHeaders,
    )

    fun mapToManualFormData(
        prefillQuery: String?,
        prefillMode: SearchMode?,
    ): ConfirmBookManualFormData? {
        val query = prefillQuery ?: return null
        val mode = prefillMode ?: return null

        return ConfirmBookManualFormData(
            title = if (mode == SearchMode.ALL || mode == SearchMode.TITLE) query else "",
            authors = if (mode == SearchMode.AUTHOR) query else "",
            isbn13 = if (mode == SearchMode.ISBN) query else "",
        )
    }

    fun mapToTempBookForCoverPicker(
        title: String,
        authors: String,
        publishedYear: String?,
        isbn13: String?,
        source: BookSource,
        coverUrl: String?,
    ): Book = Book(
        title = title,
        authors = authors.split(",").map { it.trim() }.filter { it.isNotBlank() },
        publishedYear = publishedYear,
        isbn13 = isbn13,
        source = source,
        coverUrl = coverUrl,

        // Not necessary for now. Add later if necessary for book cover selection:
        id = "",
        sourceId = null,
        status = ReadingStatus.NOT_STARTED,
        createdAtEpochMs = 0,
        lastOpenAtEpochMs = 0,
        lastMarkedToDelete = 0,
        toBeDeleted = false,
        coverRequestHeaders = emptyMap(),
    )
}
