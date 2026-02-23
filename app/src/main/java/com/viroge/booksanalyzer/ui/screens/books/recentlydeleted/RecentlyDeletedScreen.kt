package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.viroge.booksanalyzer.ui.components.BookSourceBadge
import com.viroge.booksanalyzer.ui.components.CommonAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.components.CommonItemCard
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyDeletedScreen(
    state: RecentlyDeletedUiState,
    onRestoreBook: (String) -> Unit,
    onBack: () -> Unit,
) {

    var showRestoreBookDialog by remember { mutableStateOf(false) }
    var selectedBookData by remember { mutableStateOf(Pair("", "")) } // first: id, second: title

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = "Recently Deleted",
                canGoBack = true,
                onBack = onBack,
            )
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            if (state.books.isEmpty()) {
                Spacer(Modifier.height(height = 16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "No Recently Deleted Books",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(height = 8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Books you delete will appear here for 7 days before being permanently removed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                ) {
                    items(
                        items = state.books,
                        key = { it.id },
                    ) { book ->

                        CommonItemCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectedBookData = Pair(book.id, book.title)
                                showRestoreBookDialog = true
                            },
                        ) {
                            Row(
                                modifier = Modifier.padding(all = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                            ) {
                                CommonAsyncImage(
                                    url = book.coverUrl,
                                    requestHeaders = book.coverRequestHeaders,
                                    size = CommonAsyncImageSize.XSMALL,
                                )

                                Column(modifier = Modifier.weight(weight = 1f)) {

                                    Text(
                                        text = book.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    if (book.authors.isNotEmpty()) {
                                        Spacer(Modifier.height(height = 4.dp))
                                        Text(
                                            text = book.authors.joinToString(separator = ", "),
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }

                                    val meta = listOfNotNull(
                                        book.publishedYear?.toString(),
                                        book.isbn13,
                                    ).joinToString(separator = " • ")

                                    if (meta.isNotBlank()) {
                                        Spacer(Modifier.height(height = 4.dp))
                                        Text(
                                            text = meta,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(height = 8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Spacer(modifier = Modifier.weight(weight = 1f))
                                        Text(
                                            text = "Source:",
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        BookSourceBadge(
                                            source = book.source,
                                            modifier = Modifier.padding(all = 2.dp),
                                            showFullSourceName = false,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showRestoreBookDialog) {
                AlertDialog(
                    onDismissRequest = { showRestoreBookDialog = false },
                    title = {
                        Text(text = "Restore Book")
                    },
                    text = {
                        Text(text = "Would you like to restore \"${selectedBookData.second}\" back to your collection?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showRestoreBookDialog = false
                                onRestoreBook(selectedBookData.first)
                            }) { Text(text = "Restore") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showRestoreBookDialog = false
                            }) { Text(text = "Cancel") }
                    },
                )
            }
        }
    }
}
