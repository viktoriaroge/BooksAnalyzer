package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
) {

    val vm: BookDetailsViewModel = hiltViewModel()

    val state by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->

            when (event) {
                is BookDetailEvent.Deleted -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Deleted: ${event.title}",
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        vm.undoDelete()
                    } else {
                        onBack() // go back to Library if not undone
                    }
                }
            }
        }
    }

    BookDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onStatusChange = vm::setStatus,
        onDelete = vm::delete,
    )
}

