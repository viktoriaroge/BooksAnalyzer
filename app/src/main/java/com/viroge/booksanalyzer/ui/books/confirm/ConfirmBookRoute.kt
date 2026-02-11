package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.books.add.AddBookFlowViewModel
import com.viroge.booksanalyzer.ui.nav.Routes

@Composable
fun ConfirmBookRoute(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onBookSaved: (String) -> Unit,
) {
    val vm: ConfirmBookViewModel = hiltViewModel()

    val parentEntry = remember(entry) {
        navController.getBackStackEntry(Routes.ADD_BOOK_FLOW)
    }
    val flowVm: AddBookFlowViewModel = hiltViewModel(parentEntry)

    val candidate by flowVm.selectedCandidate.collectAsState()
    val prefill by flowVm.prefillQuery.collectAsState()
    val isSaving by vm.isSaving.collectAsState()
    val error by vm.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->
            when (event) {
                is ConfirmEvent.Saved -> {
                    if (!event.wasInserted) {
                        snackbarHostState.showSnackbar(message = "Already in your library — opening it.")
                    }
                    flowVm.clear()
                    onBookSaved(event.bookId)
                }

                is ConfirmEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    ConfirmBookScreen(
        candidate = candidate,
        snackbarHostState = snackbarHostState,
        prefillQuery = prefill,
        isSaving = isSaving,
        error = error,
        onBack = onBack,
        onConfirmSave = {
            candidate?.let(vm::saveCandidate)
            // TODO: manual add later
        }
    )
}
