package com.viroge.booksanalyzer.ui.screens.books.library.collection

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi
import com.viroge.booksanalyzer.ui.screens.books.library.BookStatusBadge
import com.viroge.booksanalyzer.ui.screens.books.library.LibraryFloatingActionButton

@Composable
fun CollectionScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: CollectionScreenState.Content,
    screenValues: CollectionScreenValues,
    values: ContentStateValues,
    filters: CollectionFilters,
    query: String,
    fullListState: LazyListState,
    showSearch: Boolean,
    onToggleSearch: () -> Unit,
    onHideSearch: () -> Unit,
    onToggleFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onQueryChange: (String) -> Unit,
    onToggleLibraryView: () -> Unit,
    onOpenBook: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(screenValues.screenName),
                actions = {
                    IconButton(onClick = onToggleSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = onToggleFilters) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            LibraryFloatingActionButton(
                isFullLibrary = true,
                fabShowFullText = stringResource(values.fabText),
                onClick = onToggleLibraryView,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            ActiveFiltersRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                values = values,
                filters = filters,
                onClearFilters = onClearFilters,
            )

            val focusRequester = remember { FocusRequester() }
            if (showSearch) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = { Text(text = stringResource(values.searchPlaceholder)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (query.isBlank()) onHideSearch()
                            else onQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    },
                )
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }

            if (state.allBooks.isEmpty()) {
                Spacer(Modifier.height(height = 16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(values.emptyStateTitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(height = 8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = customAnnotatedString(values.emptyStateText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

            } else {
                LazyColumn(
                    state = fullListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                ) {
                    items(
                        items = state.allBooks,
                        key = { it.id },
                        contentType = { "library_book_item" },
                    ) { book ->
                        BookRowCard(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            book = book,
                            onClick = { onOpenBook(book.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    values: ContentStateValues,
    filters: CollectionFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filterText = remember(filters, values) {
        val parts = mutableListOf<String>()

        filters.status?.let {
            parts.add(it.label.asString(context))
        }
        if (filters.sort != CollectionSortUi.Added) {
            val sortLabel = filters.sort.label.asString(context)
            parts.add(context.getString(values.activeSortText, sortLabel))
        }

        parts.joinToString(separator = " • ")
    }
    if (filterText.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = filterText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = onClearFilters,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(text = stringResource(values.clearFilterText))
        }
    }
}

@Composable
private fun BookRowCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: CollectionBookData,
    onClick: () -> Unit,
) {
    PvItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {

            PvBookCoverAsyncImage(
                url = book.url,
                requestHeaders = book.headers,
                imageSize = PvBookCoverImageSize.Small,
                // Animation parameters:
                animate = true,
                animationKey = book.animationKey,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
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
                        text = book.authors,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (book.meta?.trim()?.isNotBlank() ?: false) {
                    Spacer(Modifier.height(height = 4.dp))
                    Text(
                        text = book.meta,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(modifier = Modifier.height(height = 8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BookStatusBadge(
                        modifier = Modifier.padding(all = 2.dp),
                        statusText = book.status.label.asString(),
                        statusColor = when (book.status) {
                            BookReadingStatusUi.Abandoned -> MaterialTheme.colorScheme.errorContainer
                            BookReadingStatusUi.Finished -> MaterialTheme.colorScheme.tertiaryContainer
                            BookReadingStatusUi.NotStarted -> MaterialTheme.colorScheme.surfaceContainerHighest
                            BookReadingStatusUi.Reading -> MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f))
                    PvBookSourceBadge(
                        modifier = Modifier.padding(all = 2.dp),
                        sourceText = book.source.shortLabel.asString(),
                    )
                }
            }
        }
    }
}
