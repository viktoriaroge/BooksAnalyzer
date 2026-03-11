package com.viroge.booksanalyzer.ui.screens.books.library

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import javax.inject.Inject

class LibraryMapper @Inject constructor() {

    fun getScreenValues(): LibraryScreenValues = LibraryScreenValues(
        screenName = R.string.library_screen_name,
    )

    fun mapToData(
        book: Book,
    ): LibraryBookData = LibraryBookData(
        id = book.id,
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.publishedYear,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        url = book.coverUrl,
        headers = book.coverRequestHeaders,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
