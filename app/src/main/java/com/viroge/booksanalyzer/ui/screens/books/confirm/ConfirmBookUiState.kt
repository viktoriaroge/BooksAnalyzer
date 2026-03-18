package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class ConfirmBookUiState(
    val screenState: ConfirmBookScreenState = ConfirmBookScreenState(),
    val bookData: ConfirmBookDataState? = null,
)

@Immutable
data class ConfirmBookScreenState(
    val isSaving: Boolean = false,
    val isInManualMode: Boolean = false,

    val editState: ConfirmBookEditState = ConfirmBookEditState(),

    val screenValues: ConfirmBookScreenValues = ConfirmBookScreenValues(),
)

@Immutable
data class ConfirmBookDataState(
    val animationKey: String,
    val title: String,
    val authors: String,
    val isbn13: String?,
    val source: BookSourceUi,
    val url: String?,
    val headers: Map<String, String>,
)

@Immutable
data class ConfirmBookEditState(
    val editTitle: String = "",
    val showTitleError: Boolean = false,
    val editAuthors: String = "",
    val showAuthorError: Boolean = false,
    val editYear: String = "",
    val editIsbn13: String = "",
)

@Immutable
data class ConfirmBookScreenValues(
    @param:StringRes val screenTitleConfirm: Int = R.string.empty_text,
    @param:StringRes val screenTitleManual: Int = R.string.empty_text,
    @param:StringRes val isbnLabel: Int = R.string.empty_text,
    @param:StringRes val sourceLabel: Int = R.string.empty_text,
    @param:StringRes val saveButtonLabel: Int = R.string.empty_text,
    @param:StringRes val genericErrorMessage: Int = R.string.empty_text,
    @param:StringRes val titleRequiredError: Int = R.string.empty_text,

    @param:StringRes val manualInstruction: Int = R.string.empty_text,
    @param:StringRes val manualTitleLabel: Int = R.string.empty_text,
    @param:StringRes val manualTitleError: Int = R.string.empty_text,
    @param:StringRes val manualAuthorLabel: Int = R.string.empty_text,
    @param:StringRes val manualAuthorError: Int = R.string.empty_text,
    @param:StringRes val manualYearLabel: Int = R.string.empty_text,
    @param:StringRes val manualIsbn13Label: Int = R.string.empty_text,
    @param:StringRes val manualCoverUrlLabel: Int = R.string.empty_text,
    @param:StringRes val manualSaveButtonLabel: Int = R.string.empty_text,

    @param:StringRes val changeCoverButtonLabel: Int = R.string.empty_text,
)
