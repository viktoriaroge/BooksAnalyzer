package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class BookSearchUiState(
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val query: String = "",
    val mode: BookSearchModeUi = BookSearchModeUi.All,
    val recent: List<String> = emptyList(),
    val screenState: SearchScreenState = SearchScreenState.Idle(
        recentSearchesValues = RecentSearchesValues(),
        searchHistoryDialogValues = SearchHistoryDialogValues(),
    ),
    val screenValues: SearchScreenValues = SearchScreenValues(),
)

sealed interface SearchScreenState {

    @Immutable
    data object Loading : SearchScreenState

    @Immutable
    data class Idle(
        val recentSearchesValues: RecentSearchesValues,
        val searchHistoryDialogValues: SearchHistoryDialogValues,
    ) : SearchScreenState

    @Immutable
    data class Empty(
        val query: String,
        val emptyStateValues: EmptyStateValues,
    ) : SearchScreenState

    @Immutable
    data class Error(
        val errorStateValues: ErrorStateValues,
    ) : SearchScreenState

    @Immutable
    data class Content(
        val query: String,
        val items: List<SearchBookDataState>,
        val contentStateValues: ContentStateValues,

        val showError: Boolean = false,
        val errorStateValues: ErrorStateValues = ErrorStateValues(),
    ) : SearchScreenState
}

@Immutable
data class SearchBookDataState(
    val animationKey: String,
    val title: String,
    val authors: String,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val meta: String,
    val source: BookSourceUi,
    val sourceId: String?,
    val url: String?,
    val headers: Map<String, String>,
)

@Immutable
data class SearchScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val searchFieldHint: Int = R.string.empty_text,
)

@Immutable
data class RecentSearchesValues(
    @param:StringRes val sectionText: Int = R.string.empty_text,
    @param:StringRes val clearButtonText: Int = R.string.empty_text,
)

@Immutable
data class SearchHistoryDialogValues(
    @param:StringRes val title: Int = R.string.empty_text,
    @param:StringRes val text: Int = R.string.empty_text,
    @param:StringRes val clearButtonText: Int = R.string.empty_text,
    @param:StringRes val cancelButtonText: Int = R.string.empty_text,
)

@Immutable
data class ErrorStateValues(
    @param:StringRes val errorStateTitle: Int = R.string.empty_text,
    val errorStateText: UiText = UiText.StringResource(R.string.empty_text),
    @param:StringRes val errorStateButton: Int = R.string.empty_text,
)

@Immutable
data class EmptyStateValues(
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
    @param:StringRes val emptyStateButton: Int = R.string.empty_text,
)

@Immutable
data class ContentStateValues(
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val additionalSuggestionText: Int = R.string.empty_text,
    @param:StringRes val loadMoreDefaultButtonText: Int = R.string.empty_text,
    @param:StringRes val loadMoreInProgressButtonText: Int = R.string.empty_text,
    @param:StringRes val manualButtonText: Int = R.string.empty_text,
)
