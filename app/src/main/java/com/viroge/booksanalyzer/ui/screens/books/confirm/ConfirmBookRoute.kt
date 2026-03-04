package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.nav.Routes
import com.viroge.booksanalyzer.ui.screens.books.add.AddBookFlowViewModel
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerViewModel

@Composable
fun ConfirmBookRoute(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onBookSaved: (String) -> Unit,
) {

    val parentEntry = remember(entry) {
        navController.getBackStackEntry(Routes.ADD_BOOK)
    }
    val flowVm: AddBookFlowViewModel = hiltViewModel(viewModelStoreOwner = parentEntry)
    val book by flowVm.selectedBook.collectAsState()
    val prefill by flowVm.prefillQuery.collectAsState()
    val prefillMode by flowVm.prefillMode.collectAsState()

    val vm: ConfirmBookViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    val coverPickerVM: CoverPickerViewModel = hiltViewModel()
    val coverPickerState by coverPickerVM.state.collectAsState()
    val usePickerCover = coverPickerState.initialized

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
        isSaving = state.isSaving,
        error = state.error,
        book = book,
        selectedCoverUrl =
            if (usePickerCover) coverPickerState.selectedCover.url
            else null,
        headersForBookCover =
            if (usePickerCover) coverPickerState.selectedCover.headers
            else book?.coverRequestHeaders ?: emptyMap(),
        prefillQuery = prefill,
        prefillMode = prefillMode,
        onOpenCoverPicker = { book?.let(coverPickerVM::openCoverPicker) },
        onBack = onBack,
        onConfirmSave = {
            book?.let {
                vm.saveBook(
                    book = it,
                    selectedCoverUrl = if (usePickerCover) coverPickerState.selectedCover.url else null,
                )
            }
        },
        onConfirmSaveManual = { title, authors, year, isbn13, coverUrl ->
            vm.saveManualBook(
                title = title,
                authors = authors,
                publishedYear = year,
                isbn13 = isbn13,
                coverUrl = coverUrl,
                selectedCoverUrl = if (usePickerCover) coverPickerState.selectedCover.url else null,
            )
        },
    )

    CoverPickerSheet(
        state = coverPickerState,
        onManualUrlChange = coverPickerVM::onManualUrlChange,
        onAddManualUrl = coverPickerVM::addManualUrl,
        onSelect = coverPickerVM::selectCover,
        onDismiss = coverPickerVM::closeCoverPicker,
    )
}
