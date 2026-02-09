package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = !isSaving,
            ) { Text(text = "Back") }

            Button(
                onClick = onConfirmSave,
                enabled = !isSaving && candidate?.let { true } ?: false,
            ) { Text(text = "Save") }
        }

        if (isSaving) {
            Spacer(Modifier.height(height = 12.dp))

            LinearProgressIndicator(
                Modifier.fillMaxWidth(),
            )
        }

        error?.let {
            Spacer(Modifier.height(height = 12.dp))

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(height = 16.dp))

        when {
            candidate != null -> {
                Text(
                    text = "Confirm book",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    Modifier.height(height = 8.dp),
                )

                AsyncImage(
                    model = candidate.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 240.dp),
                )

                Text(
                    text = candidate.title,
                    style = MaterialTheme.typography.titleMedium,
                )

                if (candidate.authors.isNotEmpty()) {
                    Text(
                        text = candidate.authors
                            .joinToString(separator = ", "),
                    )
                }

                candidate.isbn13?.let {
                    Text(text = "ISBN-13: $it")
                }
            }

            !prefillQuery.isNullOrBlank() -> {
                Text(
                    text = "Manual add is next",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(
                    Modifier.height(height = 8.dp),
                )

                Text(text = "We’ll add a form soon. For now, go back and select a result.")
            }

            else -> {
                Text(text = "Nothing to confirm.")
            }
        }
    }
}
