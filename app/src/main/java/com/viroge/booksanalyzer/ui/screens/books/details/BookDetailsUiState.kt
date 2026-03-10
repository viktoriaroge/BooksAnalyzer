package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

data class BookDetailsUiState(
    val screenState: BookDetailsScreenState = BookDetailsScreenState(),
    val bookData: BookDetailsDataState? = null,
)

data class BookDetailsScreenState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isSaving: Boolean = false,
    val isInEditMode: Boolean = false,

    val editState: BookDetailsEditState = BookDetailsEditState(),

    val screenValues: BookDetailsScreenValues = BookDetailsScreenValues(),
    val editScreenValues: BookDetailsEditScreenValues = BookDetailsEditScreenValues(),
    val deleteDialogValues: BookDetailsDeleteDialogValues = BookDetailsDeleteDialogValues(),
)

data class BookDetailsEditState(
    val editTitle: String = "",
    val showTitleError: Boolean = false,
    val editAuthors: String = "",
    val showAuthorError: Boolean = false,
    val editYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
)

data class BookDetailsDataState(
    val id: String,
    val title: String,
    val authors: String,
    val year: String?,
    val isbn13: String?,
    val isbn10: String?,
    val url: String?,
    val headers: Map<String, String>,
    val status: BookReadingStatusUi = BookReadingStatusUi.NotStarted,
    val source: BookSourceUi = BookSourceUi.Manual,
)

data class BookDetailsScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val originLabel: Int = R.string.empty_text,
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
)

data class BookDetailsEditScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val originLabel: Int = R.string.empty_text,
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
    @param:StringRes val changeCoverButtonText: Int = R.string.empty_text,
    @param:StringRes val titleLabel: Int = R.string.empty_text,
    @param:StringRes val titleError: Int = R.string.empty_text,
    @param:StringRes val authorLabel: Int = R.string.empty_text,
    @param:StringRes val authorError: Int = R.string.empty_text,
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
