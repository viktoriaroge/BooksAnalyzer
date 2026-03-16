package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.screens.books.cover.BookCoverPickerSheet
import com.viroge.booksanalyzer.ui.screens.books.cover.CoverPickerViewModel

@Composable
fun BookDetailsRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
) {
    val sharedVM: MainSharedViewModel = activityViewModel()

    val coverPickerVM: CoverPickerViewModel = hiltViewModel()
    val coverPickerState by coverPickerVM.state.collectAsState()

    val vm: BookDetailsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    BackHandler(enabled = true) {
        if (state.screenState is BookDetailsScreenState.Edit) vm.exitEditMode()
        else onBack()
    }

    val context = LocalContext.current
    val snackbar = LocalAppSnackbar.current
    LaunchedEffect(key1 = Unit) {
        vm.events.collect { event ->
            when (event) {
                is BookDetailsEvent.Error -> {
                    snackbar.show(message = event.errorType.message.asString(context), duration = SnackbarDuration.Short)
                }
            }
        }
    }

    when (val screenState = state.screenState) {
        is BookDetailsScreenState.Content -> {
            var showDeleteDialog by remember { mutableStateOf(false) }

            BookDetailsScreen(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = screenState,
                onBack = onBack,
                onStatusChange = vm::updateStatus,
                onDelete = remember { { showDeleteDialog = true } },
                onEdit = vm::enterEditMode,
            )

            if (showDeleteDialog) {
                val bookData = screenState.bookData
                val dialogValues = screenState.deleteDialogValues

                AlertDialog(
                    onDismissRequest = remember { { showDeleteDialog = false } },
                    title = { Text(text = stringResource(dialogValues.title)) },
                    text = { Text(text = customAnnotatedString(dialogValues.message.asString())) },
                    confirmButton = {
                        TextButton(
                            onClick = remember {
                                {
                                    showDeleteDialog = false

                                    sharedVM.markToDelete(
                                        bookId = bookData.id,
                                        title = bookData.title,
                                    )
                                    onBack()
                                }
                            }) { Text(text = stringResource(dialogValues.deleteButtonText)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = remember { { showDeleteDialog = false } })
                        { Text(text = stringResource(dialogValues.cancelButtonText)) }
                    },
                )
            }
        }

        is BookDetailsScreenState.Edit -> {
            val bookData = screenState.bookData

            BookDetailsEditScreen(
                state = screenState,
                onSaveEdits = vm::saveEdits,
                onCancelEdit = vm::exitEditMode,
                onUpdateEditTitle = vm::updateEditTitle,
                onUpdateEditAuthors = vm::updateEditAuthors,
                onUpdateEditPublishedYear = vm::updateEditPublishedYear,
                onUpdateEditIsbn13 = vm::updateEditIsbn13,
                onUpdateEditIsbn10 = vm::updateEditIsbn10,
                onOpenCoverPicker = remember {
                    {
                        coverPickerVM.openCoverPicker(
                            originalCoverUrl = bookData.url,
                            originalCoverRequestHeaders = bookData.headers,
                            source = bookData.source.domainSource,
                            isbn13 = bookData.isbn13,
                        )
                    }
                }
            )

            BookCoverPickerSheet(
                state = coverPickerState,
                onManualUrlChange = coverPickerVM::onManualUrlChange,
                onAddManualUrl = coverPickerVM::addManualUrl,
                onSelect = coverPickerVM::selectCover,
                onDismiss = coverPickerVM::closeCoverPicker,
            )
        }
    }
}
