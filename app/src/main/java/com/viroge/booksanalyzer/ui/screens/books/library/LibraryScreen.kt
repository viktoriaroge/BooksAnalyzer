package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi

@Composable
fun LibraryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    screenValues: LibraryScreenValues,
    state: LibraryScreenState.Content,
    contentStateValues: ContentStateValues,
    filters: LibraryFilters,
    query: String,
    currentListState: LazyListState,
    fullListState: LazyListState,
    showSearch: Boolean,
    onToggleSearch: () -> Unit,
    onHideSearch: () -> Unit,
    onToggleFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onQueryChange: (String) -> Unit,
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
                values = contentStateValues,
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
                    placeholder = { Text(text = stringResource(contentStateValues.searchPlaceholder)) },
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

            // Content with books
            LazyColumn(
                state = fullListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            ) {
                // Currently Reading section
                if (state.currentBooks.isNotEmpty()) {
                    stickyHeader(key = "currently-reading-section-title") {
                        Surface(Modifier.fillMaxWidth()) {
                            SectionHeader(text = stringResource(contentStateValues.currentlyReadingSectionTitle).uppercase())
                        }
                    }
                    item(
                        key = "currently-reading-section-content",
                        contentType = { "currently-reading-section-content" },
                    ) {
                        LazyRow(
                            state = currentListState,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        ) {
                            items(
                                items = state.currentBooks,
                                key = { it.id },
                            ) { book ->
                                CurrentlyReadingCard(
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    book = book,
                                    onClick = { onOpenBook(book.id) },
                                )
                            }
                        }
                    }
                }

                // A list of all saved books
                if (state.allBooks.isNotEmpty()) {
                    stickyHeader(key = "all-books-section-title") {
                        Surface(Modifier.fillMaxWidth()) {
                            SectionHeader(text = stringResource(contentStateValues.allBooksSectionTitle).uppercase())
                        }
                    }
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
fun SectionHeader(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ActiveFiltersRow(
    values: ContentStateValues,
    filters: LibraryFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val filterText = remember(filters, values) {
        val parts = mutableListOf<String>()

        filters.status?.let {
            parts.add(it.label.asString(context))
        }
        if (filters.sort != LibrarySortUi.Added) {
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
fun CurrentlyReadingCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: LibraryBookData,
    onClick: () -> Unit
) {
    PvItemCard(
        modifier = Modifier.width(width = 210.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PvBookCoverAsyncImage(
                    url = book.url,
                    requestHeaders = book.headers,
                    size = PvBookCoverImageSize.MEDIUM,
                    modifier = Modifier.align(Alignment.Center),
                    animate = true,
                    animationKey = book.animationKey,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )

                PvBookSourceBadge(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(all = 2.dp),
                    sourceText = book.source.shortLabel.asString(),
                )
            }

            PvLinearProgressIndicator(progress = { 0.3F })

            Text(
                text = book.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (book.authors.isNotEmpty()) {
                Text(
                    text = book.authors,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BookRowCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: LibraryBookData,
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
                size = PvBookCoverImageSize.SMALL,
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
