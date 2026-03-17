package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsEditScreen(
    state: BookDetailsScreenState.Edit,
    onSaveEdits: () -> Unit,
    onCancelEdit: () -> Unit,
    onUpdateEditTitle: (String) -> Unit,
    onUpdateEditAuthors: (String) -> Unit,
    onUpdateEditPublishedYear: (String) -> Unit,
    onUpdateEditIsbn13: (String) -> Unit,
    onUpdateEditIsbn10: (String) -> Unit,
    onOpenCoverPicker: () -> Unit,
) {
    val book = state.bookData
    val values = state.editStateValues
    val editState = state.editState

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenName),
                canGoBack = true,
                onBack = onCancelEdit,
                actions = {
                    IconButton(onClick = onSaveEdits) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                        )
                    }
                },
            )
        }) { screenPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .imePadding()
                .padding(top = screenPadding.calculateTopPadding()),
        ) {

            PvBookCoverHeader(
                imageUrl = book.url,
                headersForBookCover = book.headers,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onOpenCoverPicker,
            ) {
                Text(text = stringResource(values.changeCoverButtonText))
            }

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editTitle,
                onValueChange = onUpdateEditTitle,
                label = { Text(stringResource(values.titleLabel)) },
                singleLine = true,
                isError = editState.showTitleError,
                supportingText = {
                    if (editState.showTitleError) {
                        Text(
                            text = stringResource(values.titleError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editAuthors,
                onValueChange = onUpdateEditAuthors,
                label = { Text(stringResource(values.authorLabel)) },
                singleLine = true,
                placeholder = { Text(stringResource(values.authorHint)) },
                isError = editState.showAuthorError,
                supportingText = {
                    if (editState.showAuthorError) {
                        Text(
                            text = stringResource(values.authorError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editYear,
                onValueChange = onUpdateEditPublishedYear,
                label = { Text(stringResource(values.yearLabel)) },
                singleLine = true,
                placeholder = { Text(stringResource(values.yearHint)) },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editIsbn13,
                onValueChange = onUpdateEditIsbn13,
                label = { Text(stringResource(values.isbn13Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editIsbn10,
                onValueChange = onUpdateEditIsbn10,
                label = { Text(stringResource(values.isbn10Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(height = 16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onSaveEdits,
                enabled = !state.isSaving,
            ) {
                Text(
                    text = if (state.isSaving) stringResource(values.saveChangesInProgressButtonText)
                    else stringResource(values.saveChangesButtonText)
                )
            }

            Spacer(Modifier.height(height = 12.dp))
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onCancelEdit,
            ) {
                Text(stringResource(values.cancelChangesButtonText))
            }
        }

        Spacer(Modifier.height(height = 24.dp))
    }
}
