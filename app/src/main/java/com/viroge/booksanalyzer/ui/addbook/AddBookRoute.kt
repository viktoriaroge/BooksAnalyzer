package com.viroge.booksanalyzer.ui.addbook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.nav.Routes
import com.viroge.booksanalyzer.ui.search.BookSearchScreen
import com.viroge.booksanalyzer.ui.search.BookSearchViewModel

@Composable
fun AddBookRoute(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onGoToConfirm: () -> Unit,
) {

    val searchVm: BookSearchViewModel = hiltViewModel()

    val parentEntry = remember(entry) {
        navController.getBackStackEntry(Routes.ADD_BOOK_FLOW)
    }
    val flowVm: AddBookFlowViewModel = hiltViewModel(parentEntry)

    BookSearchScreen(
        vm = searchVm,
        onSelectCandidate = { candidate ->
            flowVm.setCandidate(candidate)
            onGoToConfirm()
        },
        onManualAdd = { prefill ->
            flowVm.setManualPrefill(prefill)
            onGoToConfirm()
        }
    )
}
