package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.viroge.booksanalyzer.R

@Immutable
data class BookCoverPickerUiState(
    val initialized: Boolean,
    val isOpen: Boolean,

    val screenValues: BookCoverPickerScreenValues,
    val screenState: BookCoverPickerScreenState,
)

sealed interface BookCoverPickerScreenState {

    @Immutable
    data object Loading : BookCoverPickerScreenState

    @Immutable
    data class Content(
        val manualUrlInput: String = "",

        val values: BookCoverPickerContentValues,

        val selectedCover: BookCoverPickerItem?,
        val bookCovers: List<BookCoverPickerItem>,
    ) : BookCoverPickerScreenState
}

@Immutable
data class BookCoverPickerScreenValues(
    @param:StringRes val screenTitle: Int = R.string.empty_text,
)

@Immutable
data class BookCoverPickerContentValues(
    @param:StringRes val inputFieldLabel: Int = R.string.empty_text,
    val inputFieldIcon: ImageVector = Icons.Default.Check,
)

@Immutable
data class BookCoverPickerItem(
    val url: String,
)
