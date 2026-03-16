package com.viroge.booksanalyzer.ui.screens.books.library

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.usecase.book.LibrarySort
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed class LibrarySortUi(
    val domainSource: LibrarySort,
    val label: UiText,
) {

    object Added : LibrarySortUi(
        domainSource = LibrarySort.ADDED,
        label = UiText.StringResource(R.string.library_sort_by_added),
    )

    object Recent : LibrarySortUi(
        domainSource = LibrarySort.RECENT,
        label = UiText.StringResource(R.string.library_sort_by_recent),
    )

    object Title : LibrarySortUi(
        domainSource = LibrarySort.TITLE,
        label = UiText.StringResource(R.string.library_sort_by_title),
    )

    object Author : LibrarySortUi(
        domainSource = LibrarySort.AUTHOR,
        label = UiText.StringResource(R.string.library_sort_by_author),
    )

    companion object {
        fun allOptions(): List<LibrarySortUi> = listOf(
            Added,
            Recent,
            Title,
            Author,
        )

        fun fromDomain(sort: LibrarySort?): LibrarySortUi {
            return when (sort) {
                LibrarySort.ADDED -> Added
                LibrarySort.RECENT -> Recent
                LibrarySort.TITLE -> Title
                LibrarySort.AUTHOR -> Author
                null -> Added // Default fallback if sort is missing
            }
        }
    }
}
