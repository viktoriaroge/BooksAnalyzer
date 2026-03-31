package com.viroge.booksanalyzer.ui.screens.books.scanner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ScanBookBarcodeViewModel @Inject constructor(
    mapper: ScanBookBarcodeMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(ScanBookBarcodeUiState(screenValues = mapper.getScreenValues()))
    val state = _state.asStateFlow()

    fun onIsbnDetected(isbn: String) {
        // TODO: If isbn detected, use to search for the book, no need to stay on this screen

        if (_state.value.isProcessing) return

        _state.update { it.copy(isProcessing = true) }

        val cleanedIsbn = isbn.trim().replace("-", "")

        _state.update {
            it.copy(
                scannedIsbn = cleanedIsbn,
                isProcessing = false,
            )
        }
    }

    fun resetScanner() {
        _state.update { it.copy(scannedIsbn = null, isProcessing = false) }
    }
}
