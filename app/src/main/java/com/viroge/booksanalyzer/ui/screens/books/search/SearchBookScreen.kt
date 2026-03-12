package com.viroge.booksanalyzer.ui.screens.books.search

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: SearchUiState,
    query: String,
    mode: SearchMode,
    recent: List<String>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (SearchMode) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onManualAdd: (String) -> Unit,
    onSelectBook: (Book) -> Unit,
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { PvTopAppBar(title = stringResource(R.string.search_screen_name)) },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            SearchModeChips(
                selected = mode,
                onSelect = { onModeChanged(it) },
            )

            OutlinedTextField(
                value = query,
                onValueChange = { onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.search_screen_search_field_hint)) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }
                }
            )

            when (state) {
                is SearchUiState.Idle -> {
                    RecentSearchesSection(
                        values = state.recentSearchesValues,
                        recent = recent,
                        onPick = onQueryChanged,
                        onDeleteOne = onRemoveRecentSearch,
                        onClearAll = onClearRecentSearches,
                    )
                }

                SearchUiState.Loading -> {
                    Spacer(Modifier.height(height = 4.dp))
                    PvLinearProgressIndicator()
                }

                is SearchUiState.Error -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onRefresh() }) {
                        Text(text = stringResource(state.errorStateValues.refreshButtonText))
                    }
                }

                is SearchUiState.Empty -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = customAnnotatedString(state.emptyStateValues.noResultsText, state.query),
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onManualAdd(state.query) }) {
                        Text(text = stringResource(state.emptyStateValues.manualButtonText))
                    }
                }

                is SearchUiState.Partial -> {
                    BooksList(
                        contentStateValues = state.contentStateValues,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        query = state.query,
                        items = state.items,
                        onSelect = onSelectBook,
                        canLoadMore = canLoadMore,
                        isLoadingMore = isLoadingMore,
                        onLoadMore = onLoadMore,
                        onManualAdd = onManualAdd,
                        showingPartialResults = true,
                    )
                }

                is SearchUiState.Success -> {
                    BooksList(
                        contentStateValues = state.contentStateValues,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        query = state.query,
                        items = state.items,
                        onSelect = onSelectBook,
                        canLoadMore = canLoadMore,
                        isLoadingMore = isLoadingMore,
                        onLoadMore = onLoadMore,
                        onManualAdd = onManualAdd,
                    )
                }
            }
        }
    }
}

@Composable
fun SearchModeChips(
    selected: SearchMode,
    onSelect: (SearchMode) -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        for (mode in SearchMode.entries) {
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(text = SearchModeMapper.getUiModel(mode).text) },
            )
        }
    }
}

@Composable
private fun BooksList(
    contentStateValues: ContentStateValues,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    query: String,
    items: List<Book>,
    onSelect: (Book) -> Unit,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onManualAdd: (String) -> Unit,
    showingPartialResults: Boolean = false,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        items(items) { book ->
            PvItemCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelect(book) },
            ) {
                Row(
                    modifier = Modifier.padding(all = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                ) {
                    with(sharedTransitionScope) {
                        PvBookCoverAsyncImage(
                            url = book.coverUrl,
                            requestHeaders = book.coverRequestHeaders,
                            size = PvBookCoverImageSize.XSMALL,
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(
                                    key = BookTransitionKey.calculate(book.title, book.authors, book.isbn13)
                                ),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        )
                    }

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
                            book.publishedYear,
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
                                text = stringResource(contentStateValues.sourceLabel),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            PvBookSourceBadge(
                                source = book.source,
                                modifier = Modifier.padding(all = 2.dp),
                                showFullSourceName = showingPartialResults,
                            )
                        }
                    }
                }
            }
        }

        item {
            if (showingPartialResults) {
                Spacer(Modifier.height(height = 8.dp))
                Text(text = stringResource(contentStateValues.partialResultsText))
            }

            Spacer(Modifier.height(height = 8.dp))
            Text(text = stringResource(contentStateValues.loadMoreSuggestionText))

            if (canLoadMore) {
                Spacer(Modifier.height(height = 8.dp))
                Button(
                    onClick = { onLoadMore() },
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text =
                            if (isLoadingMore) stringResource(contentStateValues.loadMoreInProgressButtonText)
                            else stringResource(contentStateValues.loadMoreDefaultButtonText)
                    )
                }
            }

            Spacer(Modifier.height(height = 8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onManualAdd(query) },
            ) {
                Text(text = stringResource(contentStateValues.manualButtonText))
            }

            Spacer(Modifier.height(height = 16.dp))
        }
    }
}
