package com.viroge.booksanalyzer.ui.screens.books.library.collection

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.usecase.book.LibrarySort
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed class CollectionSortUi(
    val domainSource: LibrarySort,
    val label: UiText,
) {

    object Added : CollectionSortUi(
        domainSource = LibrarySort.ADDED,
        label = UiText.StringResource(R.string.collection_sort_by_added),
    )

    object Recent : CollectionSortUi(
        domainSource = LibrarySort.RECENT,
        label = UiText.StringResource(R.string.collection_sort_by_recent),
    )

    object Title : CollectionSortUi(
        domainSource = LibrarySort.TITLE,
        label = UiText.StringResource(R.string.collection_sort_by_title),
    )

    object Author : CollectionSortUi(
        domainSource = LibrarySort.AUTHOR,
        label = UiText.StringResource(R.string.collection_sort_by_author),
    )

    companion object {
        fun allOptions(): List<CollectionSortUi> = listOf(
            Added,
            Recent,
            Title,
            Author,
        )

        fun fromDomain(sort: LibrarySort?): CollectionSortUi {
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
