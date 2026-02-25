package com.viroge.booksanalyzer.ui.screens.books.details

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.ui.components.BookSourceBadge
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.components.CommonCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.StatusMapper
import com.viroge.booksanalyzer.ui.screens.customAnnotatedString

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
        containerColor = MaterialTheme.colorScheme.surface, topBar = {
            CommonTopAppBar(
                title = if (state.isEditMode) stringResource(R.string.book_details_screen_in_edit_screen_name)
                else stringResource(R.string.book_details_screen_name),
                canGoBack = true,
                onBack = if (state.isEditMode) onCancelEdit else onBack,
                actions = {
                    if (state.book != null) {
                        if (state.isEditMode) {
                            IconButton(onClick = onSaveEdits) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "",
                                )
                            }
                        } else {
                            IconButton(onClick = onEdit) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "",
                                )
                            }
                        }
                    }
                },
            )
        }) { screenPadding ->

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
                val coverToShow = selectedCoverUrl ?: book.coverUrl ?: ""
                BookCoverHeader(
                    imageUrl = coverToShow,
                    headersForBookCover = headersForBookCover,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.isEditMode) {
                Button(
                    onClick = onOpenCoverPicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                ) {
                    Text(text = stringResource(R.string.book_details_screen_in_edit_change_book_cover_button_label))
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
                    CommonLinearProgressIndicator()
                    return@Column
                }

                if (state.isEditMode) {
                    OutlinedTextField(
                        value = state.editTitle,
                        onValueChange = onUpdateEditTitle,
                        label = { Text(stringResource(R.string.book_details_screen_in_edit_title_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.editAuthors,
                        onValueChange = onUpdateEditAuthors,
                        label = { Text(stringResource(R.string.book_details_screen_in_edit_author_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.book_details_screen_in_edit_author_hint)) },
                    )
                    OutlinedTextField(
                        value = state.editPublishedYear,
                        onValueChange = onUpdateEditPublishedYear,
                        label = { Text(stringResource(R.string.book_details_screen_in_edit_year_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.book_details_screen_in_edit_year_hint)) },
                    )
                    OutlinedTextField(
                        value = state.editIsbn13,
                        onValueChange = onUpdateEditIsbn13,
                        label = { Text(stringResource(R.string.book_details_screen_in_edit_isbn13_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.editIsbn10,
                        onValueChange = onUpdateEditIsbn10,
                        label = { Text(stringResource(R.string.book_details_screen_in_edit_isbn10_label)) },
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
                        Text(
                            text = if (state.isSaving) stringResource(R.string.book_details_screen_in_edit_save_in_progress_button)
                            else stringResource(R.string.book_details_screen_in_edit_save_default_button)
                        )
                    }
                    TextButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.book_details_screen_in_edit_cancel_button))
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
                        book.publishedYear?.toString(), book.isbn13
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
                            text = stringResource(R.string.book_details_screen_source_label),
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
                        Text(
                            text = if (state.isDeleting) stringResource(R.string.book_details_screen_delete_in_progress_button)
                            else stringResource(R.string.book_details_screen_delete_default_button)
                        )
                    }
                }
            }

            Spacer(Modifier.height(height = 16.dp))

            if (showDeleteDialog) {
                val settingsTabName = stringResource(R.string.settings_screen_name)
                val recentlyDeletedSectionName = stringResource(R.string.recently_deleted_screen_name)

                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = stringResource(R.string.book_details_screen_delete_book_dialog_title))
                    },
                    text = {
                        Text(
                            text = customAnnotatedString(
                                R.string.book_details_screen_delete_book_dialog_text, recentlyDeletedSectionName, settingsTabName
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDelete()
                            }) { Text(text = stringResource(R.string.book_details_screen_delete_book_dialog_delete_button)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                            }) { Text(text = stringResource(R.string.book_details_screen_delete_book_dialog_cancel_button)) }
                    },
                )
            }
        }
    }
}

@Composable
fun BookCoverHeader(
    imageUrl: String,
    headersForBookCover: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds() // Prevents the blur from bleeding out
    ) {
        // Hazy background:
        val isDarkTheme = isSystemInDarkTheme()
        CommonCoverAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 30.dp)
                .drawWithContent {
                    drawContent()
                    // Adjust slightly so the foreground pops
                    drawRect(
                        if (isDarkTheme) Color.Black.copy(alpha = 0.3f)
                        else Color.White.copy(alpha = 0.3f)
                    )
                },
            contentScale = ContentScale.Crop,
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = CommonAsyncImageSize.XXLARGE,
        )

        // Cover image:
        CommonCoverAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 32.dp)
                .shadow(12.dp, RoundedCornerShape(12.dp)),
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = CommonAsyncImageSize.XXLARGE,
        )
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
            text = stringResource(R.string.book_details_screen_status_label),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(Modifier.height(height = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {

            OutlinedTextField(
                value = StatusMapper.getUiModel(current).text,
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

                    DropdownMenuItem(text = { Text(text = StatusMapper.getUiModel(status).text) }, onClick = {
                        expanded = false
                        onChange(status)
                    })
                }
            }
        }
    }
}
