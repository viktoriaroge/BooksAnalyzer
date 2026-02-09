package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.viroge.booksanalyzer.domain.BookCandidate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    candidate: BookCandidate?,
    prefillQuery: String?,
    isSaving: Boolean,
    error: String?,
    onBack: () -> Unit,
    onConfirmSave: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Confirm Book")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) { Text(text = "←") }
                },
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(all = 16.dp),
        ) {

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
                    AsyncImage(
                        model = candidate.coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = 240.dp),
                    )

                    Spacer(Modifier.height(height = 16.dp))

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

            Spacer(
                Modifier.height(height = 8.dp),
            )

            Button(
                onClick = onConfirmSave,
                enabled = !isSaving && candidate?.let { true } ?: false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
            ) { Text(text = "Save") }
        }
    }
}
