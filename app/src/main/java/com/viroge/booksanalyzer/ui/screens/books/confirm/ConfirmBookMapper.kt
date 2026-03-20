package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
        manualTitleError = R.string.confirm_book_screen_manual_form_title_error,
        manualAuthorLabel = R.string.confirm_book_screen_manual_form_author_label,
        manualAuthorError = R.string.confirm_book_screen_manual_form_author_error,
        manualYearLabel = R.string.confirm_book_screen_manual_form_year_label,
        manualIsbn13Label = R.string.confirm_book_screen_manual_form_isbn13_label,
        manualCoverUrlLabel = R.string.confirm_book_screen_manual_form_cover_url_label,
        manualSaveButtonLabel = R.string.confirm_book_screen_manual_form_save_button_label,
    )

    fun mapToDataState(
        book: TempBook,
        selectedCandidate: BookCoverCandidate?,
    ): ConfirmBookDataState = ConfirmBookDataState(
        animationKey = BookTransitionKey.calculate(book.title, book.authors, book.isbn13, book.source, book.sourceId),
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        isbn13 = book.isbn13,
        source = BookSourceUi.fromDomain(book.source),
        url = selectedCandidate?.url ?: book.coverUrl,
    )
}
