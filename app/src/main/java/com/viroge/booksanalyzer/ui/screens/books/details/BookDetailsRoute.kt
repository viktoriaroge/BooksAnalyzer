package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerViewModel

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
) {

    val sharedVM: MainSharedViewModel = activityViewModel()
    val vm: BookDetailsViewModel = hiltViewModel()
    val coverPickerVM: CoverPickerViewModel = hiltViewModel()

    val state by vm.state.collectAsState()
    val coverPickerState by coverPickerVM.state.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    when {
        state.isInEditMode -> BookDetailsEditScreen(
            state = state,
            selectedCoverUrl = coverPickerState.selectedCover.url,
            headersForBookCover = coverPickerState.selectedCover.headers,
            onSaveEdits = {
                vm.saveEdits(
                    selectedCoverUrl = coverPickerState.selectedCover.url,
                    selectedCoverHeaders = coverPickerState.selectedCover.headers,
                )
            },
            onCancelEdit = vm::exitEditMode,
            onUpdateEditTitle = vm::updateEditTitle,
            onUpdateEditAuthors = vm::updateEditAuthors,
            onUpdateEditPublishedYear = vm::updateEditPublishedYear,
            onUpdateEditIsbn13 = vm::updateEditIsbn13,
            onUpdateEditIsbn10 = vm::updateEditIsbn10,
            onOpenCoverPicker = { state.book?.let(coverPickerVM::openCoverPicker) }
        )

        else -> BookDetailsScreen(
            state = state,
            selectedCoverUrl = coverPickerState.selectedCover.url,
            headersForBookCover = coverPickerState.selectedCover.headers,
            onBack = onBack,
            onStatusChange = vm::setStatus,
            onDelete = { showDeleteDialog = true },
            onEdit = vm::enterEditMode,
        )
    }

    CoverPickerSheet(
        state = coverPickerState,
        onManualUrlChange = coverPickerVM::onManualUrlChange,
        onAddManualUrl = coverPickerVM::addManualUrl,
        onSelect = coverPickerVM::selectCover,
        onDismiss = coverPickerVM::closeCoverPicker,
    )

    if (showDeleteDialog) {
        val dialogValues = state.deleteDialogValues
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(dialogValues.title))
            },
            text = {
                Text(
                    text = customAnnotatedString(dialogValues.message.asString())
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        state.book?.let {
                            sharedVM.markToDelete(book = it)
                            onBack()
                        }
                    }) { Text(text = stringResource(dialogValues.deleteButtonText)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }) { Text(text = stringResource(dialogValues.cancelButtonText)) }
            },
        )
    }
}

