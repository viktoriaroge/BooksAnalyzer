package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
) {

    val sharedVM: MainSharedViewModel = activityViewModel()
    val vm: BookDetailsViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()

    LaunchedEffect(key1 = Unit) {
        vm.updateLastOpenDelayed()
    }

    BookDetailsScreen(
        state = state,
        onBack = onBack,
        onStatusChange = vm::setStatus,
        onDelete = {
            sharedVM.delete(book = state.book ?: return@BookDetailsScreen)
            onBack()
        },
        onEdit = vm::enterEditMode,
        onSaveEdits = vm::saveEdits,
        onCancelEdit = vm::exitEditMode,
        onUpdateEditTitle = vm::updateEditTitle,
        onUpdateEditAuthors = vm::updateEditAuthors,
        onUpdateEditPublishedYear = vm::updateEditPublishedYear,
        onUpdateEditIsbn13 = vm::updateEditIsbn13,
        onUpdateEditIsbn10 = vm::updateEditIsbn10,
        onUpdateEditCoverUrl = vm::updateEditCoverUrl,
        onUpdateEditStatus = vm::updateEditStatus,
    )
}

