package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class LibraryUiState(
    val currentBooks: List<LibraryBookData> = emptyList(),
    val allBooks: List<LibraryBookData> = emptyList(),

    val query: String = "",
    val selectedStatus: BookReadingStatusUi? = null,
    val sortState: LibrarySortUi = LibrarySortUi.Added,

    val screenValues: LibraryScreenValues = LibraryScreenValues(),
)

data class LibraryScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

data class LibraryFilters(
    val status: BookReadingStatusUi? = null, // null = All
    val sort: LibrarySortUi = LibrarySortUi.Added,
)

data class LibraryBookData(
    val id: String,
    val title: String,
    val authors: String,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val url: String?,
    val headers: Map<String, String>,
    val status: BookReadingStatusUi = BookReadingStatusUi.NotStarted,
    val source: BookSourceUi = BookSourceUi.Manual,
)
