package com.viroge.booksanalyzer.ui.books.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.nav.Routes

@Composable
fun AddBookRoute(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onGoToConfirm: () -> Unit,
) {

    val searchVm: SearchBookViewModel = hiltViewModel()

    val parentEntry = remember(key1 = entry) {
        navController.getBackStackEntry(Routes.ADD_BOOK_FLOW)
    }
    val flowVm: AddBookFlowViewModel = hiltViewModel(viewModelStoreOwner = parentEntry)

    BookSearchScreen(
        vm = searchVm,
        onLoadMore = { searchVm.loadMore() },
        onQueryChanged = { searchVm.changeQuery(newValue = it) },
        onSelectCandidate = { candidate ->
            flowVm.setCandidate(candidate)
            onGoToConfirm()
        },
        onManualAdd = { prefill ->
            flowVm.setManualPrefill(prefill)
            onGoToConfirm()
        },
        onBack = onBack,
    )
}
