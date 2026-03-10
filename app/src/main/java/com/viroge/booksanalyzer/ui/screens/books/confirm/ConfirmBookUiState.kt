package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class ConfirmBookUiState(
    val screenState: ConfirmBookScreenState = ConfirmBookScreenState(),
    val bookData: ConfirmBookDataState? = null,

    val selectedBook: Book? = null, // TODO: To be later removed
)

data class ConfirmBookScreenState(
    val isSaving: Boolean = false,
    val screenValues: ConfirmBookScreenValues = ConfirmBookScreenValues(),
    val manualFormData: ConfirmBookManualFormData? = null,
    val titleInput: String = "",
    val authorsInput: String = "",
    val yearInput: String = "",
    val isbn13Input: String = "",
)

data class ConfirmBookDataState(
    val title: String,
    val authors: String,
    val isbn13: String?,
    val source: BookSourceUi,
    val url: String?,
    val headers: Map<String, String>,
)

data class ConfirmBookManualFormData(
    val title: String,
    val authors: String,
    val isbn13: String,
)

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
    @param:StringRes val manualAuthorLabel: Int = R.string.empty_text,
    @param:StringRes val manualYearLabel: Int = R.string.empty_text,
    @param:StringRes val manualIsbn13Label: Int = R.string.empty_text,
    @param:StringRes val manualCoverUrlLabel: Int = R.string.empty_text,
    @param:StringRes val manualSaveButtonLabel: Int = R.string.empty_text,

    @param:StringRes val changeCoverButtonLabel: Int = R.string.empty_text,
)
