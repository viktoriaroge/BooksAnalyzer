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
    )

    fun getContentStateValues(): ContentStateValues = ContentStateValues(
        fullCollectionFabText = R.string.library_screen_fab_show_full_collection_label,
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
        headers = book.coverRequestHeaders,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
