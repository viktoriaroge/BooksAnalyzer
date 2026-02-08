package com.viroge.booksanalyzer.ui.library

import androidx.compose.runtime.Composable

@Composable
fun LibraryRoute(
    onAddBook: () -> Unit,
    onOpenBook: (String) -> Unit,
) {

    LibraryScreen(
        onAddBook = onAddBook,
        onOpenBook = onOpenBook,
    )
}
