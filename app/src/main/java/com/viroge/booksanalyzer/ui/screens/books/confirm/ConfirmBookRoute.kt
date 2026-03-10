package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerViewModel

@Composable
fun ConfirmBookRoute(
    onBack: () -> Unit,
    onBookSaved: () -> Unit,
) {
    val coverPickerVM: CoverPickerViewModel = hiltViewModel()
    val coverPickerState by coverPickerVM.state.collectAsState()

    val vm: ConfirmBookViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val bookData = state.bookData

    BackHandler(enabled = true) {
        vm.clearSessionData()
        onBack()
    }

    val context = LocalContext.current
    val snackbar = LocalAppSnackbar.current
    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->
            when (event) {
                is ConfirmEvent.Saved -> {
                    vm.clearSessionData()
                    onBookSaved()
                }

                is ConfirmEvent.Error -> {
                    snackbar.show(message = event.errorType.message.asString(context), duration = SnackbarDuration.Short)
                }
            }
        }
    }

    ConfirmBookScreen(
        state = state,
        onOpenCoverPicker = {
            bookData ?: return@ConfirmBookScreen

            coverPickerVM.openCoverPicker(
                originalCoverUrl = bookData.url,
                originalCoverRequestHeaders = bookData.headers,
                source = bookData.source.domainSource,
                isbn13 = bookData.isbn13,
            )
        },
        onBack = {
            vm.clearSessionData()
            onBack()
        },
        onSave = vm::saveBook,
    )

    ConfirmManualBookScreen(
        state = state,
        onBack = {
            vm.clearSessionData()
            onBack()
        },
        onTitleChange = vm::onTitleChange,
        onAuthorsChange = vm::onAuthorsChange,
        onYearChange = vm::onYearChange,
        onIsbnChange = vm::onIsbnChange,
        onOpenCoverPicker = {
            val tempBook = vm.getTemporaryBookForCoverPicker()
            coverPickerVM.openCoverPicker(
                originalCoverUrl = tempBook.coverUrl,
                originalCoverRequestHeaders = tempBook.coverRequestHeaders,
                source = tempBook.source,
                isbn13 = tempBook.isbn13,
            )
        },
        onSave = vm::saveManualBook,
    )

    CoverPickerSheet(
        state = coverPickerState,
        onManualUrlChange = coverPickerVM::onManualUrlChange,
        onAddManualUrl = coverPickerVM::addManualUrl,
        onSelect = coverPickerVM::selectCover,
        onDismiss = coverPickerVM::closeCoverPicker,
    )
}
