package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.ReadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    state: BookDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onStatusChange: (ReadingStatus) -> Unit,
    onDelete: () -> Unit,
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Book Details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) { Text(text = "←") }
                },
            )
        }
    ) { padding ->

        val book = state.book

        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (book == null) {
                Text(text = "Loading…")
                return@Column
            }

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = book.coverUrl)
                    .crossfade(enable = true)
                    .build(),
                error = painterResource(id = R.drawable.blank_book),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
            )

            if (book.authors.isNotBlank()) {
                Text(text = book.authors)
            }

            val meta = listOfNotNull(
                book.publishedYear?.toString(),
                book.isbn13
            ).joinToString(separator = " • ")

            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            StatusPicker(
                current = runCatching { ReadingStatus.valueOf(value = book.status) }
                    .getOrDefault(defaultValue = ReadingStatus.NOT_STARTED),
                onChange = onStatusChange,
            )

            Spacer(Modifier.height(height = 8.dp))

            Button(
                onClick = { showDeleteDialog = true },
                enabled = !state.isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
            ) {
                Text(text = if (state.isDeleting) "Deleting…" else "Delete book")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(text = "Delete book?")
                },
                text = {
                    Text(text = "This will remove the book from your library. This can’t be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDelete()
                        }) { Text(text = "Delete") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                        }) { Text(text = "Cancel") }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusPicker(
    current: ReadingStatus,
    onChange: (ReadingStatus) -> Unit,
) {

    var expanded by remember { mutableStateOf(value = false) }

    Column {

        Text(
            text = "Status",
            style = MaterialTheme.typography.titleSmall,
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {

            OutlinedTextField(
                value = current.name
                    .replace(oldChar = '_', newChar = ' ')
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {

                ReadingStatus.entries.forEach { status ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = status.name
                                    .replace(oldChar = '_', newChar = ' ')
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() })
                        },
                        onClick = {
                            expanded = false
                            onChange(status)
                        }
                    )
                }
            }
        }
    }
}
