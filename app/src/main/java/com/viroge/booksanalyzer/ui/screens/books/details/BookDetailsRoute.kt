package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.domain.CoverUrlOptimizer
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel
import com.viroge.booksanalyzer.ui.screens.bookcover.CoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.bookcover.CoverPickerViewModel

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
) {

    val sharedVM: MainSharedViewModel = activityViewModel()
    val vm: BookDetailsViewModel = hiltViewModel()
    val coverPickerVM: CoverPickerViewModel = hiltViewModel()

    val state by vm.ui.collectAsState()
    val coverPicker by coverPickerVM.coverPicker.collectAsState()
    val selectedCover by coverPickerVM.selectedCover.collectAsState()

    LaunchedEffect(key1 = Unit) {
        vm.updateLastOpenDelayed()
    }

    BookDetailsScreen(
        state = state,
        headersForBookCover =
            if (selectedCover.isSelected) selectedCover.headers
            else state.book?.coverUrl?.let { CoverUrlOptimizer.getCoverHeaders(it) } ?: emptyMap(),
        selectedCoverUrl = if (selectedCover.isSelected) selectedCover.url else null,
        onBack = onBack,
        onStatusChange = vm::setStatus,
        onDelete = {
            sharedVM.markToDelete(book = state.book ?: return@BookDetailsScreen)
            onBack()
        },
        onEdit = vm::enterEditMode,
        onSaveEdits = { vm.saveEdits(selectedCoverUrl = if (selectedCover.isSelected) selectedCover.url else null) },
        onCancelEdit = vm::exitEditMode,
        onUpdateEditTitle = vm::updateEditTitle,
        onUpdateEditAuthors = vm::updateEditAuthors,
        onUpdateEditPublishedYear = vm::updateEditPublishedYear,
        onUpdateEditIsbn13 = vm::updateEditIsbn13,
        onUpdateEditIsbn10 = vm::updateEditIsbn10,
        onUpdateEditStatus = vm::updateEditStatus,
        onOpenCoverPicker = { state.book?.let(coverPickerVM::openCoverPicker) }
    )

    CoverPickerSheet(
        state = coverPicker,
        selectedUrl = if (selectedCover.isSelected) selectedCover.url else null,
        onManualUrlChange = coverPickerVM::onManualUrlChange,
        onAddManualUrl = coverPickerVM::addManualUrl,
        onSelect = coverPickerVM::selectCover,
        onDismiss = coverPickerVM::closeCoverPicker,
    )
}

