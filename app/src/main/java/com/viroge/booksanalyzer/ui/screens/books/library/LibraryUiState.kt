package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class LibraryUiState(
    val screenValues: LibraryScreenValues = LibraryScreenValues(),
    val screenState: LibraryScreenState = LibraryScreenState.Loading,
)

sealed interface LibraryScreenState {

    @Immutable
    data object Loading : LibraryScreenState

    @Immutable
    data class Empty(
        val navRoute: LibraryNavDirection = LibraryNavDirection.SEARCH,
        val emptyStateValues: EmptyStateValues,
    ) : LibraryScreenState

    @Immutable
    data class Content(
        val currentBooks: List<LibraryBookData>,
        val contentStateValues: ContentStateValues,
    ) : LibraryScreenState
}

@Immutable
data class LibraryScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

@Immutable
data class EmptyStateValues(
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
    @param:StringRes val emptyStateButton: Int = R.string.empty_text,
)

@Immutable
data class ContentStateValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val startReadingButtonText: Int = R.string.empty_text,
)

enum class LibraryNavDirection {
    SEARCH, COLLECTION
}

@Immutable
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
