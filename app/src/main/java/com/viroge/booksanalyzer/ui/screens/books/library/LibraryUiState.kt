package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class LibraryUiState(
    val currentBooks: List<LibraryBookData> = emptyList(),
    val allBooks: List<LibraryBookData> = emptyList(),
    val selectedStatus: BookReadingStatusUi? = null,
    val sortState: LibrarySortUi = LibrarySortUi.Added,
    val screenValues: LibraryScreenValues = LibraryScreenValues(),
)

data class LibraryScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val searchPlaceholder: Int = R.string.empty_text,
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
    @param:StringRes val currentlyReadingSectionTitle: Int = R.string.empty_text,
    @param:StringRes val allBooksSectionTitle: Int = R.string.empty_text,
    @param:StringRes val activeSortText: Int = R.string.empty_text,
    @param:StringRes val clearFilterText: Int = R.string.empty_text,
    @param:StringRes val filtersTitle: Int = R.string.empty_text,
    @param:StringRes val filtersClearButtonText: Int = R.string.empty_text,
    @param:StringRes val filtersStatusSelectionTitle: Int = R.string.empty_text,
    @param:StringRes val filtersStatusAllLabel: Int = R.string.empty_text,
    @param:StringRes val filtersSortSelectionTitle: Int = R.string.empty_text,
)

data class LibraryFilters(
    val status: BookReadingStatusUi? = null, // null = All
    val sort: LibrarySortUi = LibrarySortUi.Added,
)

data class LibraryBookData(
    val animationKey: String,
    val id: String,
    val title: String,
    val authors: String,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val meta: String?,
    val url: String?,
    val headers: Map<String, String>,
    val status: BookReadingStatusUi = BookReadingStatusUi.NotStarted,
    val source: BookSourceUi = BookSourceUi.Manual,
)
