package com.viroge.booksanalyzer.ui.screens.books.library.collection

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class CollectionUiState(
    val screenValues: CollectionScreenValues = CollectionScreenValues(),
    val screenState: CollectionScreenState = CollectionScreenState.Loading,
)

sealed interface CollectionScreenState {

    @Immutable
    data object Loading : CollectionScreenState

    @Immutable
    data class Content(
        val selectedStatus: BookReadingStatusUi?, // null = All
        val sortState: CollectionSortUi,
        val allBooks: List<CollectionBookData>,
        val stateValues: ContentStateValues,
        val filtersSheetValues: FiltersSheetValues,

        val isInEmptyState: Boolean = false,
        val showEmptyStateButton: Boolean = false,
    ) : CollectionScreenState
}

@Immutable
data class CollectionScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

@Immutable
data class ContentStateValues(
    @param:StringRes val searchPlaceholder: Int = R.string.empty_text,
    @param:StringRes val activeSortText: Int = R.string.empty_text,
    @param:StringRes val clearFilterText: Int = R.string.empty_text,
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
    @param:StringRes val emptyStateButton: Int = R.string.empty_text,
)

@Immutable
data class FiltersSheetValues(
    @param:StringRes val filtersTitle: Int = R.string.empty_text,
    @param:StringRes val filtersClearButtonText: Int = R.string.empty_text,
    @param:StringRes val filtersStatusSelectionTitle: Int = R.string.empty_text,
    @param:StringRes val filtersStatusAllLabel: Int = R.string.empty_text,
    @param:StringRes val filtersSortSelectionTitle: Int = R.string.empty_text,
)

@Immutable
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
    val status: BookReadingStatusUi = BookReadingStatusUi.NotStarted,
    val source: BookSourceUi = BookSourceUi.Manual,
)

@Immutable
data class CollectionFilters(
    val status: BookReadingStatusUi? = null, // null = All
    val sort: CollectionSortUi = CollectionSortUi.Added,
)
