package com.viroge.booksanalyzer.ui.screens.books.scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScanBookBarcodeRoute(
    onBack: () -> Unit,
) {
    val vm: ScanBookBarcodeViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    ScanBookBarcodeScreen(
        state = state,
        onBack = onBack,
        onIsbnDetected = vm::onIsbnDetected,
        onResetScanner = vm::resetScanner,
    )
}
