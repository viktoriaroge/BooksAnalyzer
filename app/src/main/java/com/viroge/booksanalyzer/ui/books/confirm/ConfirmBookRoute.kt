package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.books.add.AddBookFlowViewModel
import com.viroge.booksanalyzer.ui.nav.Routes
import java.util.UUID

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

    ConfirmBookScreen(
        candidate = candidate,
        prefillQuery = prefill,
        isSaving = isSaving,
        error = error,
        onBack = onBack,
        onConfirmSave = {
            val candidateToSave = candidate
            candidateToSave?.let {
                vm.saveCandidate(it) { newBookId ->
                    flowVm.clear()
                    onBookSaved(newBookId)
                }
            }
            // TODO: Later in case of null candidate, implement manual addition and saving
        }
    )
}
