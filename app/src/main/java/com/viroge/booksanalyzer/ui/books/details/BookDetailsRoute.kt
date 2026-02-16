package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.snackbar.LocalAppSnackbar

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
) {

    val vm: BookDetailsViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()

    val snackbar = LocalAppSnackbar.current

    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->
            when (event) {
                is BookDetailEvent.Deleted -> {
                    snackbar.show(
                        message = "Deleted: ${event.title}",
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                        onActionPerformed = { vm.undoDelete() },
                        onDismissed = { onBack() },
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        vm.updateLastOpenDelayed()
    }

    BookDetailsScreen(
        state = state,
        onBack = onBack,
        onStatusChange = vm::setStatus,
        onDelete = vm::delete,
        onEdit = vm::enterEditMode,
        onSaveEdits = vm::saveEdits,
        onCancelEdit = vm::exitEditMode,
        onUpdateEditTitle = vm::updateEditTitle,
        onUpdateEditAuthors = vm::updateEditAuthors,
        onUpdateEditPublishedYear = vm::updateEditPublishedYear,
        onUpdateEditIsbn13 = vm::updateEditIsbn13,
        onUpdateEditIsbn10 = vm::updateEditIsbn10,
        onUpdateEditCoverUrl = vm::updateEditCoverUrl,
        onUpdateEditStatus = vm::updateEditStatus,
    )
}

