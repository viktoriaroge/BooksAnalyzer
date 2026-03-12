package com.viroge.booksanalyzer.ui.screens.books.search

import com.viroge.booksanalyzer.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchMapper @Inject constructor() {

    fun getRecentSearchesValues(): RecentSearchesValues = RecentSearchesValues(
        sectionText = R.string.search_screen_recent_searches_section_text,
        clearButtonText = R.string.search_screen_recent_searches_clear_button,
    )

    fun getSearchHistoryDialogValues(): SearchHistoryDialogValues = SearchHistoryDialogValues(
        title = R.string.search_screen_clear_history_dialog_title,
        text = R.string.search_screen_clear_history_dialog_text,
        clearButtonText = R.string.search_screen_clear_history_clear_button,
        cancelButtonText = R.string.search_screen_clear_history_cancel_button,
    )

    fun getErrorStateValues(): ErrorStateValues = ErrorStateValues(
        refreshButtonText = R.string.search_screen_refresh_button,
    )

    fun getEmptyStateValues(): EmptyStateValues = EmptyStateValues(
        noResultsText = R.string.search_screen_no_results_error_text,
        manualButtonText = R.string.search_screen_add_manually_button,
    )

    fun getContentStateValues(): ContentStateValues = ContentStateValues(
        sourceLabel = R.string.search_screen_source_label,
        partialResultsText = R.string.search_screen_partial_results_error_text,
        loadMoreSuggestionText = R.string.search_screen_load_more_suggestion_text,
        loadMoreDefaultButtonText = R.string.search_screen_load_more_button_default_text,
        loadMoreInProgressButtonText = R.string.search_screen_load_more_button_in_progress_text,
        manualButtonText = R.string.search_screen_add_manually_button,
    )
}