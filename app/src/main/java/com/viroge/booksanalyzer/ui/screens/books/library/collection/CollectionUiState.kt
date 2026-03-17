package com.viroge.booksanalyzer.ui.screens.books.library.collection

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class CollectionUiState(
    val screenValues: CollectionScreenValues = CollectionScreenValues(),
    val screenState: CollectionScreenState = CollectionScreenState.Loading,
)

sealed interface CollectionScreenState {

    object Loading : CollectionScreenState

    data class Content(
        val selectedStatus: BookReadingStatusUi?, // null = All
        val sortState: CollectionSortUi,
        val allBooks: List<CollectionBookData>,
        val stateValues: ContentStateValues,
        val filtersSheetValues: FiltersSheetValues,
    ) : CollectionScreenState
}

data class CollectionScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

data class ContentStateValues(
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

data class CollectionBookData(
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

data class CollectionFilters(
    val status: BookReadingStatusUi? = null, // null = All
    val sort: CollectionSortUi = CollectionSortUi.Added,
)
