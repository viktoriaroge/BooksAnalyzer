package com.viroge.booksanalyzer.ui.screens.books.deleted

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
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

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(state.screenValues.screenName),
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

            when (val screenState = state.screenState) {
                RecentlyDeletedScreenState.Loading -> {
                    // Empty screen is fine for now since loading is almost instant.
                }

                is RecentlyDeletedScreenState.Empty -> {
                    EmptyStateView(screenState.values)
                }

                is RecentlyDeletedScreenState.Content -> {
                    var selectedBook by remember { mutableStateOf<RecentlyDeletedBookState?>(null) }

                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(items = screenState.books, key = { it.id }) { book ->
                            BookItemCard(
                                book = book,
                                sourceLabel = stringResource(screenState.values.sourceLabel),
                                onClick = { selectedBook = book }
                            )
                        }
                    }

                    selectedBook?.let { book ->
                        val dialogBookTitle = book.title
                        AlertDialog(
                            onDismissRequest = { selectedBook = null },
                            title = { Text(stringResource(screenState.values.restoreDialogTitle)) },
                            text = { Text(customAnnotatedString(screenState.values.restoreDialogText, dialogBookTitle)) },
                            confirmButton = {
                                TextButton(onClick = {
                                    onRestoreBook(book.id)
                                    selectedBook = null
                                }) { Text(stringResource(screenState.values.restoreButtonLabel)) }
                            },
                            dismissButton = {
                                TextButton(onClick = { selectedBook = null }) {
                                    Text(stringResource(screenState.values.cancelButtonLabel))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(values: RecentlyDeletedEmptyValues) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(height = 16.dp))
        Image(
            modifier = Modifier
                .size(260.dp)
                .padding(horizontal = 24.dp),
            painter = painterResource(R.drawable.ic_default_book),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(values.emptyStateTitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(height = 24.dp))
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = customAnnotatedString(values.emptyStateText),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(24.dp))
    }
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
                        sourceText = book.source.shortLabel.asString(),
                    )
                }
            }
        }
    }
}
