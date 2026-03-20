package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.BookSourceUi

@Immutable
data class BookDetailsUiState(
    val screenState: BookDetailsScreenState,
)

sealed interface BookDetailsScreenState {
    @Immutable
    data class Content(
        val isLoading: Boolean = true,
        val isDeleting: Boolean = false,
        val bookData: BookDetailsDataState,
        val screenValues: DetailsScreenValues = DetailsScreenValues(),
        val deleteDialogValues: BookDetailsDeleteDialogValues = BookDetailsDeleteDialogValues(),
    ) : BookDetailsScreenState

    @Immutable
    data class Edit(
        val isSaving: Boolean = false,
        val editStateValues: EditDetailsScreenValues,
        val editState: BookDetailsEditState,
        val bookData: BookDetailsDataState,
    ) : BookDetailsScreenState
}

@Immutable
data class BookDetailsEditState(
    val editTitle: String = "",
    val showTitleError: Boolean = false,
    val editAuthors: String = "",
    val showAuthorError: Boolean = false,
    val editYear: String = "",
    val editIsbn13: String = "",
    val editIsbn10: String = "",
)

@Immutable
data class BookDetailsDataState(
    val animationKey: String,
    val id: String,
    val url: String?,
    val title: String = "",
    val authors: String = "",
    val year: String? = null,
    val isbn13: String? = null,
    val isbn10: String? = null,
    val meta: String = "",
    val status: BookReadingStatusUi = BookReadingStatusUi.NotStarted,
    val source: BookSourceUi = BookSourceUi.Manual,
)

@Immutable
data class DetailsScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
    @param:StringRes val originLabel: Int = R.string.empty_text,
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
)

@Immutable
data class EditDetailsScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
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

@Immutable
data class BookDetailsDeleteDialogValues(
    @param:StringRes val title: Int = R.string.empty_text,
    val message: UiText = UiText.DynamicString(""),
    @param:StringRes val deleteButtonText: Int = R.string.empty_text,
    @param:StringRes val cancelButtonText: Int = R.string.empty_text,
)
