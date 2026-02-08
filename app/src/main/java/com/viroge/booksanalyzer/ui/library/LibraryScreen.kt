package com.viroge.booksanalyzer.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LibraryScreen(
    onAddBook: () -> Unit,
    onOpenBook: (String) -> Unit,
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Text("Library", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        Button(onClick = onAddBook) { Text("Add book") }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { onOpenBook("stub-book-id") },
        ) {
            Text("Open a stub book detail")
        }
    }
}
