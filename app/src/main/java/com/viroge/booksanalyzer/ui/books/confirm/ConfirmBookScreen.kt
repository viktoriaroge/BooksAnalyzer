package com.viroge.booksanalyzer.ui.books.confirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.SearchMode
import com.viroge.booksanalyzer.ui.common.CommonAsyncImage
import com.viroge.booksanalyzer.ui.common.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.common.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    candidate: BookCandidate?,
    prefillQuery: String?,
    prefillMode: SearchMode?,
    isSaving: Boolean,
    error: String?,
    onBack: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmSaveManual: (
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ) -> Unit = { _, _, _, _, _ -> },
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = if (candidate != null) "Confirm Book" else "Add Book Manually",
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { screenPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            if (isSaving) {
                Spacer(Modifier.height(height = 12.dp))

                LinearProgressIndicator(
                    Modifier.fillMaxWidth(),
                )
            }

            error?.let { msg ->
                Spacer(Modifier.height(height = 12.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(height = 16.dp))

            when {
                candidate != null -> {
                    CommonAsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        url = candidate.coverUrl,
                        size = CommonAsyncImageSize.LARGE,
                    )

                    Spacer(Modifier.height(height = 24.dp))

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

                    Spacer(Modifier.height(height = 8.dp))

                    Button(
                        onClick = onConfirmSave,
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                    ) { Text(text = "Save") }

                    Spacer(Modifier.height(height = 16.dp))
                }

                !prefillQuery.isNullOrBlank() -> {
                    ManualBookForm(
                        prefillQuery = prefillQuery,
                        prefillMode = prefillMode ?: SearchMode.ALL,
                        isSaving = isSaving,
                        onSave = onConfirmSaveManual,
                    )
                }

                else -> {
                    Text(text = "Nothing to confirm.")
                }
            }
        }
    }
}

@Composable
private fun ManualBookForm(
    prefillQuery: String,
    prefillMode: SearchMode,
    isSaving: Boolean,
    onSave: (
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ) -> Unit,
) {
    val initialTitle = when (prefillMode) {
        SearchMode.ALL, SearchMode.TITLE -> prefillQuery
        else -> ""
    }
    val initialAuthors = when (prefillMode) {
        SearchMode.AUTHOR -> prefillQuery
        else -> ""
    }
    val initialIsbn13 = when (prefillMode) {
        SearchMode.ISBN -> prefillQuery
        else -> ""
    }

    var title by remember(prefillQuery, prefillMode) { mutableStateOf(initialTitle) }
    var authors by remember(prefillQuery, prefillMode) { mutableStateOf(initialAuthors) }
    var yearText by remember { mutableStateOf("") }
    var isbn13 by remember(prefillQuery, prefillMode) { mutableStateOf(initialIsbn13) }
    var coverUrl by remember { mutableStateOf("") }

    Text(
        text = "Enter the book details. Title is required.",
        style = MaterialTheme.typography.bodyMedium,
    )

    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Title") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = authors,
        onValueChange = { authors = it },
        label = { Text("Authors (comma-separated)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = yearText,
        onValueChange = { yearText = it },
        label = { Text("Year") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = isbn13,
        onValueChange = { isbn13 = it },
        label = { Text("ISBN-13 (optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = coverUrl,
        onValueChange = { coverUrl = it },
        label = { Text("Cover URL (optional)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    Spacer(Modifier.height(16.dp))

    Button(
        onClick = {
            val year = yearText.trim().toIntOrNull()
            onSave(
                title.trim(),
                authors.trim(),
                year,
                isbn13.trim().takeIf { it.isNotBlank() },
                coverUrl.trim().takeIf { it.isNotBlank() },
            )
        },
        enabled = !isSaving && title.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
    ) { Text(text = "Save") }

    Spacer(Modifier.height(height = 16.dp))
}
