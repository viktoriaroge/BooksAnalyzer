package com.viroge.booksanalyzer.ui.screens.books

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed class BookSourceUi(
    val domainSource: BookSource,
    val label: UiText,
    val shortLabel: UiText,
) {
    object GoogleBooks : BookSourceUi(
        domainSource = BookSource.GOOGLE_BOOKS,
        label = UiText.StringResource(R.string.book_source_full_google_books),
        shortLabel = UiText.StringResource(R.string.book_source_short_google_books),
    )

    object OpenLibrary : BookSourceUi(
        domainSource = BookSource.OPEN_LIBRARY,
        label = UiText.StringResource(R.string.book_source_full_open_library),
        shortLabel = UiText.StringResource(R.string.book_source_short_open_library),
    )

    object Manual : BookSourceUi(
        domainSource = BookSource.MANUAL,
        label = UiText.StringResource(R.string.book_source_full_added_manually),
        shortLabel = UiText.StringResource(R.string.book_source_short_added_manually),
    )

    companion object {
        fun allOptions(): List<BookSourceUi> = listOf(
            GoogleBooks,
            OpenLibrary,
            Manual
        )

        fun fromDomain(source: BookSource?): BookSourceUi {
            return when (source) {
                BookSource.GOOGLE_BOOKS -> GoogleBooks
                BookSource.OPEN_LIBRARY -> OpenLibrary
                BookSource.MANUAL -> Manual
                null -> Manual // Default fallback if source is missing
            }
        }
    }
}
