package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R
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
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize()
                .padding(horizontal = 16.dp),
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
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(data = candidate.coverUrl)
                            .crossfade(enable = true)
                            .build(),
                        error = painterResource(id = R.drawable.blank_book),
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
