package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.SearchMode

@Composable
fun ManualBookForm(
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
        text = stringResource(R.string.confirm_book_screen_manual_form_instruction_text),
        style = MaterialTheme.typography.bodyMedium,
    )

    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text(stringResource(R.string.confirm_book_screen_manual_form_title_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = authors,
        onValueChange = { authors = it },
        label = { Text(stringResource(R.string.confirm_book_screen_manual_form_author_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = yearText,
        onValueChange = { yearText = it },
        label = { Text(stringResource(R.string.confirm_book_screen_manual_form_year_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = isbn13,
        onValueChange = { isbn13 = it },
        label = { Text(stringResource(R.string.confirm_book_screen_manual_form_isbn13_label)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )

    OutlinedTextField(
        value = coverUrl,
        onValueChange = { coverUrl = it },
        label = { Text(stringResource(R.string.confirm_book_screen_manual_form_cover_url_label)) },
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
        modifier = Modifier.fillMaxWidth(),
    ) { Text(text = stringResource(R.string.confirm_book_screen_manual_form_save_button_label)) }

    Spacer(Modifier.height(height = 16.dp))
}
