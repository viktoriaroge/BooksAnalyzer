package com.viroge.booksanalyzer.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BookDetailsScreen(
    bookId: String,
    onBack: () -> Unit,
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        OutlinedButton(onClick = onBack) { Text("Back") }

        Spacer(Modifier.height(16.dp))

        Text("Book Detail", style = MaterialTheme.typography.titleLarge)

        Text("bookId = $bookId")
    }
}
