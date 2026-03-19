package com.viroge.booksanalyzer.ui.screens.books.search

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.domain.usecase.search.SearchError
import com.viroge.booksanalyzer.ui.common.util.UiText
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchMapper @Inject constructor() {

    fun getScreenValues(): SearchScreenValues = SearchScreenValues(
        screenName = R.string.search_screen_name,
        searchFieldHint = R.string.search_screen_search_field_hint,
    )

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

    fun getErrorStateValues(error: SearchError): ErrorStateValues = ErrorStateValues(
        errorStateTitle = R.string.search_screen_error_title,
        errorStateText = when (error) {
            is SearchError.Timeout -> UiText.StringResource(R.string.search_screen_error_message_timeout, error.apiSource)
            is SearchError.RateLimit -> UiText.StringResource(R.string.search_screen_error_message_rate_limit, error.apiSource)
            is SearchError.SecurityError -> UiText.StringResource(R.string.search_screen_error_message_security_error, error.apiSource)
            is SearchError.NoConnection -> UiText.StringResource(R.string.search_screen_error_message_no_connection) // no source
            is SearchError.Unknown -> UiText.StringResource(R.string.search_screen_error_message_unknown) // no source
            is SearchError.Cancelled -> UiText.StringResource(R.string.empty_text) // Ignore, show nothing
            is SearchError.None -> UiText.StringResource(R.string.empty_text) // No error, show nothing
        },
        errorStateButton = R.string.search_screen_refresh_button,
    )

    fun getEmptyStateValues(): EmptyStateValues = EmptyStateValues(
        emptyStateTitle = R.string.search_screen_no_results_error_title,
        emptyStateText = R.string.search_screen_no_results_error_text,
        emptyStateButton = R.string.search_screen_add_manually_button,
    )

    fun getContentStateValues(): ContentStateValues = ContentStateValues(
        sourceLabel = R.string.search_screen_source_label,
        additionalSuggestionText = R.string.search_screen_additional_suggestion_text,
        loadMoreDefaultButtonText = R.string.search_screen_load_more_button_default_text,
        loadMoreInProgressButtonText = R.string.search_screen_load_more_button_in_progress_text,
        manualButtonText = R.string.search_screen_add_manually_button,
    )

    fun mapToDataState(
        book: TempBook,
    ): SearchBookDataState = SearchBookDataState(
        animationKey = BookTransitionKey.calculate(book.title, book.authors, book.isbn13, book.source, book.sourceId),
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.year,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        meta = listOfNotNull(book.year, book.isbn13).joinToString(separator = " • "),
        source = BookSourceUi.fromDomain(book.source),
        sourceId = book.sourceId,
        url = book.coverUrl,
        headers = book.coverRequestHeaders ?: emptyMap(),
    )

    fun mapToTempBook(
        book: SearchBookDataState,
    ): TempBook = TempBook(
        source = book.source.domainSource,
        sourceId = book.sourceId,
        title = book.title,
        authors = book.authors.split(",").map { it.trim() },
        year = book.year,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        coverUrl = book.url,
        coverRequestHeaders = book.headers,
    )

    fun mapToTempBook(
        query: String,
        mode: SearchMode,
    ): TempBook = TempBook(
        source = BookSource.MANUAL,
        sourceId = null,
        title = when (mode) {
            SearchMode.ALL,
            SearchMode.TITLE -> normalizeForManualInput(string = query)

            SearchMode.ISBN,
            SearchMode.AUTHOR -> ""
        },
        authors = when (mode) {
            SearchMode.AUTHOR -> normalizeForManualInput(string = query)
                .split(",").map { it.trim() }.filter { it.isNotBlank() }

            SearchMode.ALL,
            SearchMode.TITLE,
            SearchMode.ISBN -> emptyList()
        },
        year = null,
        isbn13 = when (mode) {
            SearchMode.ALL,
            SearchMode.TITLE,
            SearchMode.AUTHOR -> ""

            SearchMode.ISBN -> query
        },
        isbn10 = null,
        coverUrl = null,
        coverRequestHeaders = null,
    )

    private fun normalizeForManualInput(
        string: String,
        delimiter: String = " ",
        separator: String = " ",
    ): String = string.split(delimiter)
        .joinToString(separator = separator) { word ->
            val lowercaseWord = word.lowercase()
            if (lowercaseWord.length > 3) lowercaseWord.replaceFirstChar { char -> char.titlecase() } else lowercaseWord
        }
        .replaceFirstChar { char -> char.titlecase() } // always have the first word be capitalized
}
