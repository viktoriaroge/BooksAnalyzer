package com.viroge.booksanalyzer.ui.screens.books.deleted

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R

data class RecentlyDeletedUiState(
    val screenValues: RecentlyDeletedScreenValues = RecentlyDeletedScreenValues(),
    val books: List<RecentlyDeletedBookState> = emptyList(),
)

data class RecentlyDeletedScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val emptyStateTitle: Int = R.string.empty_text,
    @param:StringRes val emptyStateSubtitle: Int = R.string.empty_text,
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val restoreDialogTitle: Int = R.string.empty_text,
    @param:StringRes val restoreDialogText: Int = R.string.empty_text,
    @param:StringRes val restoreButtonLabel: Int = R.string.empty_text,
    @param:StringRes val cancelButtonLabel: Int = R.string.empty_text,
)

data class RecentlyDeletedBookState(
    val id: String,
    val title: String,
    val authors: String,
    val metadata: String,
    val coverUrl: String?,
    val coverHeaders: Map<String, String>,
    @param:StringRes val sourceBadgeTextRes: Int,
)
