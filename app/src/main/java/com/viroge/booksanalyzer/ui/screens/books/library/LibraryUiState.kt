package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class LibraryUiState(
    val screenValues: LibraryScreenValues = LibraryScreenValues(),
    val screenState: LibraryScreenState = LibraryScreenState.Loading,
)

sealed interface LibraryScreenState {

    data object Loading : LibraryScreenState

    data class Empty(
        val emptyStateValues: EmptyStateValues,
    ) : LibraryScreenState

    data class Content(
        val currentBooks: List<LibraryBookData>,
        val contentStateValues: ContentStateValues,
    ) : LibraryScreenState
}

data class LibraryScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

data class EmptyStateValues(
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
)

data class ContentStateValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
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
