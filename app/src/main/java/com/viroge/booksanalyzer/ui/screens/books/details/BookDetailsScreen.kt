package com.viroge.booksanalyzer.ui.screens.books.details

import android.util.Log
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.ui.components.BookSourceBadge
import com.viroge.booksanalyzer.ui.components.CommonAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.components.CommonItemCard
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    state: BookDetailsUiState,
    headersForBookCover: Map<String, String>,
    selectedCoverUrl: String?,
    onBack: () -> Unit,
    onStatusChange: (ReadingStatus) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onSaveEdits: () -> Unit,
    onCancelEdit: () -> Unit,
    onUpdateEditTitle: (String) -> Unit,
    onUpdateEditAuthors: (String) -> Unit,
    onUpdateEditPublishedYear: (String) -> Unit,
    onUpdateEditIsbn13: (String) -> Unit,
    onUpdateEditIsbn10: (String) -> Unit,
    onUpdateEditStatus: (ReadingStatus) -> Unit,
    onOpenCoverPicker: () -> Unit,
) {

    Log.d("BookDetailsScreen", "state: $state")

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = if (state.isEditMode) "Edit Book" else "Book Details",
                canGoBack = true,
                onBack = if (state.isEditMode) onCancelEdit else onBack,
                actions = {
                    if (state.book != null) {
                        if (state.isEditMode) {
                            IconButton(onClick = onSaveEdits) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                )
                            }
                        } else {
                            IconButton(onClick = onEdit) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                )
                            }
                        }
                    }
                },
            )
        }
    ) { screenPadding ->

        val book = state.book
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(top = screenPadding.calculateTopPadding())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {

            if (book != null) {
                CommonItemCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraSmall,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    val coverToShow = selectedCoverUrl ?: book.coverUrl
                    CommonAsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        url = coverToShow,
                        requestHeaders = headersForBookCover,
                        size = CommonAsyncImageSize.XXLARGE,
                    )
                }
            }

            if (state.isEditMode) {
                Spacer(Modifier.height(height = 8.dp))

                Button(
                    onClick = onOpenCoverPicker,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Choose a better cover")
                }

                Spacer(Modifier.height(height = 8.dp))
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

                if (state.isEditMode) {
                    OutlinedTextField(
                        value = state.editTitle,
                        onValueChange = onUpdateEditTitle,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.editAuthors,
                        onValueChange = onUpdateEditAuthors,
                        label = { Text("Authors") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g.: Jane Doe, John Smith") },
                    )
                    OutlinedTextField(
                        value = state.editPublishedYear,
                        onValueChange = onUpdateEditPublishedYear,
                        label = { Text("Year") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g.: 2020") },
                    )
                    OutlinedTextField(
                        value = state.editIsbn13,
                        onValueChange = onUpdateEditIsbn13,
                        label = { Text("ISBN-13") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.editIsbn10,
                        onValueChange = onUpdateEditIsbn10,
                        label = { Text("ISBN-10") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    StatusPicker(
                        current = state.editStatus ?: ReadingStatus.NOT_STARTED,
                        onChange = onUpdateEditStatus,
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        onClick = onSaveEdits,
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = if (state.isSaving) "Saving…" else "Save changes")
                    }
                    TextButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Cancel")
                    }
                } else {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    if (book.authors.isNotEmpty()) {
                        Text(
                            text = book.authors.joinToString(separator = ", "),
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

                    Spacer(modifier = Modifier.height(height = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Source:",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        BookSourceBadge(
                            source = book.source,
                            modifier = Modifier.padding(all = 2.dp),
                            showFullSourceName = true,
                        )
                        Spacer(modifier = Modifier.weight(weight = 1f))
                    }

                    StatusPicker(
                        current = book.status,
                        onChange = onStatusChange,
                    )

                    Spacer(Modifier.height(height = 8.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        enabled = !state.isDeleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = if (state.isDeleting) "Deleting…" else "Delete book")
                    }
                }
            }

            Spacer(Modifier.height(height = 16.dp))

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = "Delete Book")
                    },
                    text = {
                        Text(text = "Are you sure you want to remove this book from your collection? \nYou will still briefly see it in Recently Deleted in Settings.")
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
