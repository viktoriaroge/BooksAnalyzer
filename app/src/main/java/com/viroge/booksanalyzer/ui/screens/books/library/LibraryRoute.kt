package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryRoute(
    onOpenBook: () -> Unit,
) {

    val vm: LibraryViewModel = hiltViewModel()

    LibraryScreen(
        vm = vm,
        onOpenBook = { bookId ->
            vm.selectBook(bookId)
            onOpenBook()
        },
    )
}
