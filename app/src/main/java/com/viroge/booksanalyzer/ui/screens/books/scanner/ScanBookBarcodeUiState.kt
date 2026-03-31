package com.viroge.booksanalyzer.ui.screens.books.scanner

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R

@Immutable
data class ScanBookBarcodeUiState(
    val scannedIsbn: String? = null,
    val isProcessing: Boolean = false,
    val error: String? = null,

    val screenValues: ScannerScreenValues = ScannerScreenValues(),
)

@Immutable
data class ScannerScreenValues(
    @param:StringRes val screenName: Int = R.string.empty_text,
)
