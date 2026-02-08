package com.viroge.booksanalyzer.ui.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.addbook.AddBookFlowViewModel
import com.viroge.booksanalyzer.ui.nav.Routes

@Composable
fun ConfirmBookRoute(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onBookSaved: (String) -> Unit,
) {

    val parentEntry = remember(entry) {
        navController.getBackStackEntry(Routes.ADD_BOOK_FLOW)
    }
    val flowVm: AddBookFlowViewModel = hiltViewModel(parentEntry)

    val candidate by flowVm.selectedCandidate.collectAsState()
    val prefill by flowVm.prefillQuery.collectAsState()

    ConfirmBookScreen(
        candidate = candidate,
        prefillQuery = prefill,
        onBack = onBack,
        onConfirmSave = {
            // TODO: replace with actual Room insert. For now, generate ID or return a placeholder.
            val newBookId = java.util.UUID.randomUUID().toString()
            flowVm.clear()
            onBookSaved(newBookId)
        }
    )
}
