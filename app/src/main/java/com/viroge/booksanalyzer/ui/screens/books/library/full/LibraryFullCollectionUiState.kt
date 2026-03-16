package com.viroge.booksanalyzer.ui.screens.books.library.full

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class LibraryFullCollectionUiState(
    val screenValues: LibraryFullCollectionScreenValues = LibraryFullCollectionScreenValues(),
    val screenState: LibraryFullCollectionScreenState = LibraryFullCollectionScreenState.Loading,
)

sealed interface LibraryFullCollectionScreenState {

    object Loading : LibraryFullCollectionScreenState

    data class Content(
        val selectedStatus: BookReadingStatusUi?, // null = All
        val sortState: LibrarySortUi,
        val allBooks: List<LibraryBookData>,
        val fullCollectionStateValues: LibraryFullCollectionContentStateValues,
        val filtersSheetValues: FiltersSheetValues,
    ) : LibraryFullCollectionScreenState
}

data class LibraryFullCollectionScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

data class LibraryFullCollectionContentStateValues(
    @param:StringRes val fullCollectionFabText: Int = R.string.empty_text,
    @param:StringRes val searchPlaceholder: Int = R.string.empty_text,
    @param:StringRes val activeSortText: Int = R.string.empty_text,
    @param:StringRes val clearFilterText: Int = R.string.empty_text,
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
)

data class FiltersSheetValues(
    @param:StringRes val filtersTitle: Int = R.string.empty_text,
    @param:StringRes val filtersClearButtonText: Int = R.string.empty_text,
    @param:StringRes val filtersStatusSelectionTitle: Int = R.string.empty_text,
    @param:StringRes val filtersStatusAllLabel: Int = R.string.empty_text,
    @param:StringRes val filtersSortSelectionTitle: Int = R.string.empty_text,
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

data class LibraryFilters(
    val status: BookReadingStatusUi? = null, // null = All
    val sort: LibrarySortUi = LibrarySortUi.Added,
)
