package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R

data class ConfirmBookUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val bookData: ConfirmBookDataState? = null,
    val manualFormData: ConfirmBookManualFormData? = null,
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

data class ConfirmBookManualFormData(
    val initialTitle: String,
    val initialAuthors: String,
    val initialIsbn13: String,
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

    @param:StringRes val manualInstruction: Int = R.string.empty_text,
    @param:StringRes val manualTitleLabel: Int = R.string.empty_text,
    @param:StringRes val manualAuthorLabel: Int = R.string.empty_text,
    @param:StringRes val manualYearLabel: Int = R.string.empty_text,
    @param:StringRes val manualIsbn13Label: Int = R.string.empty_text,
    @param:StringRes val manualCoverUrlLabel: Int = R.string.empty_text,
    @param:StringRes val manualSaveButtonLabel: Int = R.string.empty_text,
)
