package com.viroge.booksanalyzer.ui.screens.books.deleted

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class RecentlyDeletedUiState(
    val screenValues: RecentlyDeletedScreenValues,
    val screenState: RecentlyDeletedScreenState,
)

sealed interface RecentlyDeletedScreenState {

    @Immutable
    data object Loading : RecentlyDeletedScreenState

    @Immutable
    data class Empty(
        val values: RecentlyDeletedEmptyValues,
    ) : RecentlyDeletedScreenState

    @Immutable
    data class Content(
        val values: RecentlyDeletedContentValues,
        val books: List<RecentlyDeletedBookState>,
    ) : RecentlyDeletedScreenState
}

@Immutable
data class RecentlyDeletedScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)

@Immutable
data class RecentlyDeletedEmptyValues(
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateText: Int = R.string.empty_text,
)

@Immutable
data class RecentlyDeletedContentValues(
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val restoreDialogTitle: Int = R.string.empty_text,
    @param:StringRes val restoreDialogText: Int = R.string.empty_text,
    @param:StringRes val restoreButtonLabel: Int = R.string.empty_text,
    @param:StringRes val cancelButtonLabel: Int = R.string.empty_text,
)

@Immutable
data class RecentlyDeletedBookState(
    val id: String,
    val title: String,
    val authors: String,
    val metadata: String,
    val coverUrl: String?,
    val source: BookSourceUi,
)
