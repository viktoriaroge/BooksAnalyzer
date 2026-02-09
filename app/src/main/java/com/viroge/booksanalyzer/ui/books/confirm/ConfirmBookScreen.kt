package com.viroge.booksanalyzer.ui.books.confirm

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
    isSaving: Boolean,
    error: String?,
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
                enabled = !isSaving,
            ) { Text("Back") }

            Button(
                onClick = onConfirmSave,
                enabled = !isSaving && candidate?.let { true } ?: false,
            ) { Text("Save") }
        }

        if (isSaving) {
            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                Modifier.fillMaxWidth(),
            )
        }

        error?.let {
            Spacer(Modifier.height(12.dp))

            Text(
                error,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(16.dp))

        when {
            candidate != null -> {
                Text(
                    text = "Confirm book",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    Modifier.height(8.dp),
                )

                Text(
                    text = candidate.title,
                    style = MaterialTheme.typography.titleMedium,
                )

                if (candidate.authors.isNotEmpty()) {
                    Text(
                        text = candidate.authors.joinToString(", "),
                    )
                }

                candidate.isbn13?.let {
                    Text("ISBN-13: $it")
                }
            }

            !prefillQuery.isNullOrBlank() -> {
                Text(
                    "Manual add is next",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    Modifier.height(8.dp),
                )

                Text("We’ll add a form soon. For now, go back and select a result.")
            }

            else -> {
                Text("Nothing to confirm.")
            }
        }
    }
}
