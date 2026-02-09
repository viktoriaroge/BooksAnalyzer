package com.viroge.booksanalyzer.ui.books.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryRoute(
    onAddBook: () -> Unit,
    onOpenBook: (String) -> Unit,
) {

    val vm: LibraryViewModel = hiltViewModel()
    val books = vm.books.collectAsState().value

    LibraryScreen(
        books = books,
        onAddBook = onAddBook,
        onOpenBook = onOpenBook,
    )
}
