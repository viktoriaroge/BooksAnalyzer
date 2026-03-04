package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R

data class ConfirmBookUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val bookData: ConfirmBookDataState? = null,
    val screenValues: ConfirmBookScreenValues = ConfirmBookScreenValues(),
)

data class ConfirmBookDataState(
    val title: String,
    val authors: String,
    val isbn13: String?,
    val coverUrl: String?,
    val coverHeaders: Map<String, String>,
    @param:StringRes val sourceBadgeTextRes: Int,
)

data class ConfirmBookScreenValues(
    @param:StringRes val screenTitleConfirm: Int = R.string.empty_text,
    @param:StringRes val screenTitleManual: Int = R.string.empty_text,
    @param:StringRes val changeCoverButtonLabel: Int = R.string.empty_text,
    @param:StringRes val isbnLabel: Int = R.string.empty_text,
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val saveButtonLabel: Int = R.string.empty_text,
    @param:StringRes val genericErrorMessage: Int = R.string.empty_text,
    @param:StringRes val titleRequiredError: Int = R.string.empty_text,
)
