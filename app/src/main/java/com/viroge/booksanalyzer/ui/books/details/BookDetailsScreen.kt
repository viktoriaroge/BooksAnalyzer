package com.viroge.booksanalyzer.ui.books.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.ui.common.CommonAsyncImage
import com.viroge.booksanalyzer.ui.common.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.common.CommonItemCard
import com.viroge.booksanalyzer.ui.common.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    state: BookDetailsUiState,
    onBack: () -> Unit,
    onStatusChange: (ReadingStatus) -> Unit,
    onDelete: () -> Unit,
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = "Book Details",
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { screenPadding ->

        val book = state.book

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {

            if (book != null) {
                CommonItemCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraSmall,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    CommonAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        url = book.coverUrl,
                        size = CommonAsyncImageSize.LARGE,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 12.dp),
            ) {

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                if (book == null) {
                    Text(
                        text = "Loading…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    return@Column
                }

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                if (book.authors.isNotBlank()) {
                    Text(
                        text = book.authors,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
                        Text(text = "This will remove the book from your library. This can't be undone.")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusPicker(
    current: ReadingStatus,
    onChange: (ReadingStatus) -> Unit,
) {

    var expanded by remember { mutableStateOf(value = false) }

    Column {

        Spacer(Modifier.height(height = 24.dp))
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(height = 8.dp))

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
