package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun ConfirmBookManualForm(
    formData: ConfirmBookManualFormData,
    values: ConfirmBookScreenValues,
    isSaving: Boolean,
    onSave: (
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ) -> Unit,
) {
    var title by remember(formData) { mutableStateOf(formData.initialTitle) }
    var authors by remember(formData) { mutableStateOf(formData.initialAuthors) }
    var yearText by remember { mutableStateOf("") }
    var isbn13 by remember(formData) { mutableStateOf(formData.initialIsbn13) }
    var coverUrl by remember { mutableStateOf("") }

    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(values.manualInstruction),
        style = MaterialTheme.typography.bodyMedium,
    )

    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = title,
        onValueChange = { title = it },
        label = { Text(stringResource(values.manualTitleLabel)) },
        singleLine = true,
    )

    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = authors,
        onValueChange = { authors = it },
        label = { Text(stringResource(values.manualAuthorLabel)) },
        singleLine = true,
    )

    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = yearText,
        onValueChange = { yearText = it },
        label = { Text(stringResource(values.manualYearLabel)) },
        singleLine = true,
    )

    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = isbn13,
        onValueChange = { isbn13 = it },
        label = { Text(stringResource(values.manualIsbn13Label)) },
        singleLine = true,
    )

    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = coverUrl,
        onValueChange = { coverUrl = it },
        label = { Text(stringResource(values.manualCoverUrlLabel)) },
        singleLine = true,
    )

    Spacer(Modifier.height(16.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = {
            onSave(
                title.trim(),
                authors.trim(),
                yearText.trim().toIntOrNull(),
                isbn13.trim().takeIf { it.isNotBlank() },
                coverUrl.trim().takeIf { it.isNotBlank() },
            )
        },
        enabled = !isSaving && title.isNotBlank(),
    ) {
        Text(text = stringResource(values.manualSaveButtonLabel))
    }

    Spacer(Modifier.height(24.dp))
}
