package com.viroge.booksanalyzer.ui.confirm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.BookCandidate

@Composable
fun ConfirmBookScreen(
    candidate: BookCandidate?,
    prefillQuery: String?,
    onBack: () -> Unit,
    onConfirmSave: () -> Unit,
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
            ) { Text("Back") }
            Button(
                onClick = onConfirmSave,
            ) { Text("Save") }
        }

        Spacer(Modifier.height(16.dp))

        when {
            candidate != null -> {
                Text("Confirm book", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(candidate.title, style = MaterialTheme.typography.titleMedium)
                if (candidate.authors.isNotEmpty()) Text(candidate.authors.joinToString(", "))
                candidate.isbn13?.let { Text("ISBN-13: $it") }
            }

            !prefillQuery.isNullOrBlank() -> {
                Text("Add manually", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Prefill: $prefillQuery")
                Text("TODO: manual entry form here")
            }

            else -> {
                Text("Nothing to confirm.")
                Text("Go back and search or add manually.")
            }
        }
    }
}
