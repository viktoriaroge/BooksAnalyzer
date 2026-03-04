package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.vector.ImageVector
import com.viroge.booksanalyzer.R

data class BookCoverPickerUiState(
    val initialized: Boolean = false,
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,

    val screenValues: BookCoverPickerScreenValues = BookCoverPickerScreenValues(),
    val manualUrlInput: String = "",
    val selectedCover: BookCoverState = BookCoverState(),
    val bookCovers: List<BookCoverState> = emptyList(),
    val manualBookCovers: List<BookCoverState> = emptyList(),
)

data class BookCoverPickerScreenValues(
    @param:StringRes val screenTitle: Int = R.string.empty_text,
    @param:StringRes val inputFieldLabel: Int = R.string.empty_text,
    val inputFieldIcon: ImageVector = Icons.Default.Check,
)

data class BookCoverState(
    val url: String? = null,
    val headers: Map<String, String> = emptyMap(),
)
