package com.viroge.booksanalyzer.ui.screens.books.library

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryMapper @Inject constructor() {

    fun getScreenValues(): LibraryScreenValues = LibraryScreenValues(
        screenName = R.string.library_screen_name,
    )

    fun getEmptyStateValues(): EmptyStateValues = EmptyStateValues(
        emptyStateTitle = R.string.library_screen_empty_state_title,
        emptyStateText = R.string.library_screen_empty_state_subtitle,
        emptyStateButton = R.string.library_screen_empty_state_button_text,
    )

    fun getEmptyStateNoCurrentsValues(): EmptyStateValues = EmptyStateValues(
        emptyStateTitle = R.string.library_screen_empty_state_no_current_reads_title,
        emptyStateText = R.string.library_screen_empty_state_no_current_reads_subtitle,
        emptyStateButton = R.string.library_screen_empty_state_no_current_reads_button_text,
    )

    fun getContentStateValues(): ContentStateValues = ContentStateValues(
        screenName = R.string.library_screen_name,
        startReadingButtonText = R.string.library_screen_start_reading_button,
    )

    fun mapToData(
        book: Book,
    ): LibraryBookData = LibraryBookData(
        animationKey = BookTransitionKey.calculate(book.title, book.authors, book.isbn13, book.source, book.sourceId),
        id = book.id,
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.publishedYear,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        meta = listOfNotNull(book.publishedYear, book.isbn13).joinToString(separator = " • "),
        url = book.coverUrl,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
