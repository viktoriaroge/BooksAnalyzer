package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.components.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    state: BookDetailsUiState,
    onBack: () -> Unit,
    onStatusChange: (BookReadingStatusUi) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val book = state.bookData ?: return
    if (state.screenState.isInEditMode) return

    val scrollState = rememberScrollState()
    val values = state.screenState.screenValues

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenName),
                canGoBack = true,
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                        )
                    }
                },
            )
        }) { screenPadding ->

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(top = screenPadding.calculateTopPadding())
                .fillMaxSize(),
        ) {

            if (state.screenState.isLoading) {
                PvLinearProgressIndicator()
            }

            PvBookCoverHeader(
                imageUrl = book.url,
                headersForBookCover = book.headers,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.screenState.errorState.showError) {
                Spacer(Modifier.height(height = 16.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = stringResource(state.screenState.errorState.errorMessage),
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(height = 16.dp))
            }

            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
            )

            if (book.authors.isNotEmpty()) {
                Spacer(Modifier.height(height = 12.dp))
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    text = book.authors,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            val meta = listOfNotNull(book.year, book.isbn13).joinToString(separator = " • ")
            if (meta.isNotBlank()) {
                Spacer(Modifier.height(height = 12.dp))
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(height = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
            ) {
                Text(
                    text = stringResource(values.originLabel),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                PvBookSourceBadge(
                    modifier = Modifier.padding(all = 2.dp),
                    sourceText = book.source.label.asString(),
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
            }

            StatusPicker(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                status = book.status,
                onChange = onStatusChange,
            )

            Spacer(Modifier.height(height = 16.dp))
            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                onClick = { onDelete() },
                enabled = !state.screenState.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(values.deleteButtonText))
            }

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusPicker(
    modifier: Modifier = Modifier,
    status: BookReadingStatusUi,
    onChange: (BookReadingStatusUi) -> Unit,
) {

    var expanded by remember { mutableStateOf(value = false) }
    val options = remember { BookReadingStatusUi.allOptions() }

    Column(modifier = modifier) {

        Spacer(Modifier.height(height = 24.dp))
        Text(
            text = stringResource(R.string.book_details_screen_status_label),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(Modifier.height(height = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {

            OutlinedTextField(
                value = status.label.asString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {

                options.forEach { status ->
                    DropdownMenuItem(text = { Text(text = status.label.asString()) }, onClick = {
                        expanded = false
                        onChange(status)
                    })
                }
            }
        }
    }
}
