package com.viroge.booksanalyzer.ui.books.library

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryRoute(
    onOpenBook: (String) -> Unit,
) {

    val vm: LibraryViewModel = hiltViewModel()

    LibraryScreen(
        vm = vm,
        onOpenBook = onOpenBook,
    )
}
