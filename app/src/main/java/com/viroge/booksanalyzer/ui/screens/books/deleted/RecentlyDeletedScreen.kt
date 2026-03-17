package com.viroge.booksanalyzer.ui.screens.books.deleted

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyDeletedScreen(
    state: RecentlyDeletedUiState,
    onRestoreBook: (String) -> Unit,
    onBack: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    var selectedBook by remember { mutableStateOf<RecentlyDeletedBookState?>(null) }
    val values = state.screenValues

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenName),
                canGoBack = true,
                onBack = onBack,
            )
        },
    ) { screenPadding ->
        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
                .fillMaxSize()
        ) {

            if (state.books.isEmpty()) {
                EmptyStateView(values)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = state.books, key = { it.id }) { book ->
                        BookItemCard(
                            book = book,
                            sourceLabel = stringResource(values.sourceLabel),
                            onClick = { selectedBook = book }
                        )
                    }
                }
            }

            selectedBook?.let { book ->
                AlertDialog(
                    onDismissRequest = { selectedBook = null },
                    title = { Text(stringResource(values.restoreDialogTitle)) },
                    text = { Text(customAnnotatedString(values.restoreDialogText, book.title)) },
                    confirmButton = {
                        TextButton(onClick = {
                            onRestoreBook(book.id)
                            selectedBook = null
                        }) { Text(stringResource(values.restoreButtonLabel)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedBook = null }) {
                            Text(stringResource(values.cancelButtonLabel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyStateView(values: RecentlyDeletedScreenValues) {
    Spacer(Modifier.height(16.dp))
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(values.emptyStateTitle),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(Modifier.height(8.dp))
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(values.emptyStateSubtitle),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun BookItemCard(
    book: RecentlyDeletedBookState,
    sourceLabel: String,
    onClick: () -> Unit,
) {
    PvItemCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            PvBookCoverAsyncImage(
                url = book.coverUrl,
                requestHeaders = book.coverHeaders,
                imageSize = PvBookCoverImageSize.XSmall,
            )

            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (book.authors.isNotBlank()) {
                    Spacer(Modifier.height(height = 4.dp))
                    Text(
                        text = book.authors,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (book.metadata.isNotBlank()) {
                    Spacer(Modifier.height(height = 4.dp))
                    Text(
                        text = book.metadata,
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
                        text = sourceLabel,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    PvBookSourceBadge(
                        sourceTextRes = book.sourceBadgeTextRes,
                    )
                }
            }
        }
    }
}
