package com.viroge.booksanalyzer.ui.screens.books.library.collection

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionMapper @Inject constructor() {

    fun getScreenValues(): CollectionScreenValues = CollectionScreenValues(
        screenName = R.string.collection_screen_name,
    )

    fun getContentStateValues(isLibraryEmpty: Boolean): ContentStateValues = ContentStateValues(
        searchPlaceholder = R.string.collection_screen_search_placeholder,
        activeSortText = R.string.collection_sort_explanation_prefix,
        clearFilterText = R.string.collection_screen_filter_button_clear_label,
        emptyStateTitle =
            if (isLibraryEmpty) R.string.collection_screen_empty_state_title
            else R.string.collection_screen_empty_state_from_filters_title,
        emptyStateText =
            if (isLibraryEmpty) R.string.collection_screen_empty_state_subtitle
            else R.string.collection_screen_empty_state_from_filters_subtitle,
        emptyStateButton = R.string.collection_screen_empty_state_button_text,
    )

    fun getFiltersSheetValues(): FiltersSheetValues = FiltersSheetValues(
        filtersTitle = R.string.collection_filters_sheet_title,
        filtersClearButtonText = R.string.collection_filters_sheet_button_clear_label,
        filtersStatusSelectionTitle = R.string.collection_filters_sheet_status_title,
        filtersStatusAllLabel = R.string.search_mode_all,
        filtersSortSelectionTitle = R.string.collection_filters_sheet_sort_title,
    )

    fun mapToData(
        book: Book,
    ): CollectionBookData = CollectionBookData(
        animationKey = BookTransitionKey.calculate(book.title, book.authors, book.isbn13, book.source, book.sourceId),
        id = book.id,
        title = book.title,
        authors = book.authors.joinToString(separator = ", "),
        year = book.publishedYear,
        isbn13 = book.isbn13,
        isbn10 = book.isbn10,
        meta = listOfNotNull(book.publishedYear, book.isbn13).joinToString(separator = " • "),
        url = book.coverUrl,
        source = BookSourceUi.fromDomain(book.source),
        status = BookReadingStatusUi.fromDomain(book.status),
    )
}
