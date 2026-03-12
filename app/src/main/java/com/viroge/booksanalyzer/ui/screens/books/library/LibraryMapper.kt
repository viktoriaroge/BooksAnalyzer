package com.viroge.booksanalyzer.ui.screens.books.library

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryMapper @Inject constructor() {

    fun getScreenValues(): LibraryScreenValues = LibraryScreenValues(
        screenName = R.string.library_screen_name,
        searchPlaceholder = R.string.library_screen_search_placeholder,
        emptyStateTitle = R.string.library_screen_empty_state_title,
        emptyStateText = R.string.library_screen_empty_state_subtitle,
        currentlyReadingSectionTitle = R.string.library_screen_currently_reading_section_title,
        allBooksSectionTitle = R.string.library_screen_all_section_title,
        activeSortText = R.string.library_sort_explanation_prefix,
        clearFilterText = R.string.library_screen_filter_button_clear_label,
        filtersTitle = R.string.library_filters_sheet_title,
        filtersClearButtonText = R.string.library_filters_sheet_button_clear_label,
        filtersStatusSelectionTitle = R.string.library_filters_sheet_status_title,
        filtersStatusAllLabel = R.string.search_mode_all,
        filtersSortSelectionTitle = R.string.library_filters_sheet_sort_title,
    )

    fun mapToData(
        book: Book,
    ): LibraryBookData = LibraryBookData(
        animationKey = BookTransitionKey.calculate(book.title, book.authors, book.isbn13),
        id = book.id,
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.publishedYear,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        meta = listOfNotNull(book.publishedYear, book.isbn13).joinToString(separator = " • "),
        url = book.coverUrl,
        headers = book.coverRequestHeaders,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
