package com.viroge.booksanalyzer.ui.details

import androidx.compose.runtime.Composable

@Composable
fun BookDetailsRoute(
    bookId: String,
    onBack: () -> Unit,
) {

    BookDetailsScreen(bookId = bookId, onBack = onBack)
}
