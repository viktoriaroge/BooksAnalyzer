package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.vector.ImageVector
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.provider.BookCoverState

data class BookCoverPickerUiState(
    val screenState: BookCoverPickerScreenState = BookCoverPickerScreenState(),
    val coverState: BookCoverState = BookCoverState(),
)

data class BookCoverPickerScreenState(
    val initialized: Boolean = false,
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,

    val screenValues: BookCoverPickerScreenValues = BookCoverPickerScreenValues(),
    val manualUrlInput: String = "",
)

data class BookCoverPickerScreenValues(
    @param:StringRes val screenTitle: Int = R.string.empty_text,
    @param:StringRes val inputFieldLabel: Int = R.string.empty_text,
    val inputFieldIcon: ImageVector = Icons.Default.Check,
)
