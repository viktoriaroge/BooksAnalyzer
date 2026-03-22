package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.screens.books.cover.BookCoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerViewModel

@Composable
fun ConfirmBookRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
    onBookSaved: () -> Unit,
) {
    val coverPickerVM: CoverPickerViewModel = hiltViewModel()
    val coverPickerState by coverPickerVM.state.collectAsStateWithLifecycle()

    val vm: ConfirmBookViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val book = state.bookData

    BackHandler(enabled = true) {
        onBack()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackbar = LocalAppSnackbar.current

    LaunchedEffect(vm.events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.events.collect { event ->
                when (event) {
                    is ConfirmEvent.Saved -> {
                        onBookSaved()
                    }

                    is ConfirmEvent.Error -> {
                        snackbar.show(message = event.errorType.message.asString(context), duration = SnackbarDuration.Short)
                    }
                }
            }
        }
    }

    ConfirmBookScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        state = state,
        onOpenCoverPicker = {
            book ?: return@ConfirmBookScreen

            coverPickerVM.openCoverPicker(
                selectedCoverUrl = book.coverUrl,
                originalCoverUrl = book.originalCoverUrl,
                isbn13 = book.isbn13,
                source = book.source.domainSource,
                sourceId = book.sourceId,
            )
        },
        onBack = onBack,
        onSave = vm::saveBook,
    )

    ConfirmManualBookScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        state = state,
        onBack = onBack,
        onTitleChange = vm::onTitleChange,
        onAuthorsChange = vm::onAuthorsChange,
        onYearChange = vm::onYearChange,
        onIsbnChange = vm::onIsbnChange,
        onOpenCoverPicker = {
            val tempBook = vm.getTempManualBookForCoverPicker() ?: return@ConfirmManualBookScreen

            coverPickerVM.openCoverPicker(
                selectedCoverUrl = tempBook.coverUrl,
                originalCoverUrl = tempBook.originalCoverUrl,
                isbn13 = tempBook.isbn13,
                source = tempBook.source,
                sourceId = tempBook.sourceId,
            )
        },
        onSave = vm::saveManualBook,
    )

    BookCoverPickerSheet(
        state = coverPickerState,
        onManualUrlChange = coverPickerVM::onManualUrlChange,
        onAddManualUrl = coverPickerVM::addManualUrl,
        onSelect = coverPickerVM::selectCover,
        onRemoveInvalidUrl = coverPickerVM::removeInvalidUrl,
        onDismiss = coverPickerVM::closeCoverPicker,
    )
}
