package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.annotation.StringRes

data class ConfirmBookUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val screenValues: ConfirmBookScreenValues = ConfirmBookScreenValues()
)

data class ConfirmBookScreenValues(
    @param:StringRes val genericErrorMessage: Int = 0,
    @param:StringRes val titleRequiredError: Int = 0,
)
