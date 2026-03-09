package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.ui.common.util.UiText

data class BookDetailsUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,

    val screenValues: BookDetailsScreenValues = BookDetailsScreenValues(),
    val deleteDialogValues: BookDetailsDeleteDialogValues = BookDetailsDeleteDialogValues(),
    val editState: BookDetailsEditState = BookDetailsEditState(),
    val errorState: BookDetailsErrorState = BookDetailsErrorState(),
)

data class BookDetailsScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val originLabel: Int = R.string.empty_text,
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
    @param:StringRes val changeCoverButtonText: Int = R.string.empty_text,
    @param:StringRes val titleLabel: Int = R.string.empty_text,
    @param:StringRes val authorLabel: Int = R.string.empty_text,
    @param:StringRes val authorHint: Int = R.string.empty_text,
    @param:StringRes val yearLabel: Int = R.string.empty_text,
    @param:StringRes val yearHint: Int = R.string.empty_text,
    @param:StringRes val isbn13Label: Int = R.string.empty_text,
    @param:StringRes val isbn10Label: Int = R.string.empty_text,
    @param:StringRes val saveChangesButtonText: Int = R.string.empty_text,
    @param:StringRes val saveChangesInProgressButtonText: Int = R.string.empty_text,
    @param:StringRes val cancelChangesButtonText: Int = R.string.empty_text,
)

data class BookDetailsDeleteDialogValues(
    @param:StringRes val title: Int = R.string.empty_text,
    val message: UiText = UiText.DynamicString(""),
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
    @param:StringRes val cancelButtonText: Int = R.string.empty_text,
)

data class BookDetailsEditState(
    val editTitle: String = "",
    val editAuthors: String = "",
    val editPublishedYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
)

data class BookDetailsErrorState(
    val showError: Boolean = false,
    @param:StringRes val errorMessage: Int = R.string.empty_text,
)
