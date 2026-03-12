package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.SearchMode

data class BookSearchUiState(
    val isLoadingMore: Boolean,
    val canLoadMore: Boolean,
    val query: String,
    val mode: SearchMode,
    val recent: List<String>,
    val screenState: SearchScreenState,
    val screenValues: SearchScreenValues,
)

sealed interface SearchScreenState {

    data object Loading : SearchScreenState

    data class Idle(
        val recentSearchesValues: RecentSearchesValues,
        val searchHistoryDialogValues: SearchHistoryDialogValues,
    ) : SearchScreenState

    data class Success(
        val query: String,
        val items: List<Book>,
        val contentStateValues: ContentStateValues,
    ) : SearchScreenState

    data class Partial(
        val query: String,
        val items: List<Book>,
        val messages: List<String>,
        val contentStateValues: ContentStateValues,
    ) : SearchScreenState

    data class Empty(
        val query: String,
        val emptyStateValues: EmptyStateValues,
    ) : SearchScreenState

    data class Error(
        val message: String,
        val errorStateValues: ErrorStateValues,
    ) : SearchScreenState
}

data class SearchScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val searchFieldHint: Int = R.string.empty_text,
)

data class RecentSearchesValues(
    @param:StringRes val sectionText: Int = R.string.empty_text,
    @param:StringRes val clearButtonText: Int = R.string.empty_text,
)

data class SearchHistoryDialogValues(
    @param:StringRes val title: Int = R.string.empty_text,
    @param:StringRes val text: Int = R.string.empty_text,
    @param:StringRes val clearButtonText: Int = R.string.empty_text,
    @param:StringRes val cancelButtonText: Int = R.string.empty_text,
)

data class ErrorStateValues(
    @param:StringRes val refreshButtonText: Int = R.string.empty_text,
)

data class EmptyStateValues(
    @param:StringRes val noResultsText: Int = R.string.empty_text,
    @param:StringRes val manualButtonText: Int = R.string.empty_text,
)

data class ContentStateValues(
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val partialResultsText: Int = R.string.empty_text,
    @param:StringRes val loadMoreSuggestionText: Int = R.string.empty_text,
    @param:StringRes val loadMoreDefaultButtonText: Int = R.string.empty_text,
    @param:StringRes val loadMoreInProgressButtonText: Int = R.string.empty_text,
    @param:StringRes val manualButtonText: Int = R.string.empty_text,
)
