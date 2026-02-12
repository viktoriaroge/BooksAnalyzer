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
import com.viroge.booksanalyzer.ui.snackbar.LocalAppSnackbar

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
    val flowVm: AddBookFlowViewModel = hiltViewModel(viewModelStoreOwner = parentEntry)

    val candidate by flowVm.selectedCandidate.collectAsState()
    val prefill by flowVm.prefillQuery.collectAsState()
    val isSaving by vm.isSaving.collectAsState()
    val error by vm.error.collectAsState()

    val snackbar = LocalAppSnackbar.current

    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->
            when (event) {
                is ConfirmEvent.Saved -> {
                    flowVm.clear()
                    onBookSaved(event.bookId)
                }

                is ConfirmEvent.Error -> {
                    snackbar.show(event.message)
                }
            }
        }
    }

    ConfirmBookScreen(
        candidate = candidate,
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
