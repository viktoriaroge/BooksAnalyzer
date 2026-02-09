package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookDetailsRoute(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    vm: BookDetailsViewModel = hiltViewModel(),
) {

    val state by vm.ui.collectAsState()

    BookDetailsScreen(
        state = state,
        onBack = onBack,
        onStatusChange = vm::setStatus,
        onDelete = { vm.delete(onDeleted) },
    )
}

