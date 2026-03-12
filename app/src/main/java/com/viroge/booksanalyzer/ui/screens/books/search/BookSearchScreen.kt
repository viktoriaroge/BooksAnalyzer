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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    state: BookSearchUiState,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (BookSearchModeUi) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onManualAdd: (String) -> Unit,
    onSelectBook: (SearchBookDataState) -> Unit,
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { PvTopAppBar(title = stringResource(state.screenValues.screenName)) },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            SearchModeChips(
                selected = state.mode,
                onSelect = { onModeChanged(it) },
            )

            OutlinedTextField(
                value = state.query,
                onValueChange = { onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(state.screenValues.searchFieldHint)) },
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

            when (val screenState = state.screenState) {
                is SearchScreenState.Idle -> {
                    RecentSearchesSection(
                        values = screenState.recentSearchesValues,
                        recent = state.recent,
                        onPick = onQueryChanged,
                        onDeleteOne = onRemoveRecentSearch,
                        onClearAll = onClearRecentSearches,
                    )
                }

                SearchScreenState.Loading -> {
                    Spacer(Modifier.height(height = 4.dp))
                    PvLinearProgressIndicator()
                }

                is SearchScreenState.Error -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = screenState.message,
                        color = MaterialTheme.colorScheme.error,
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onRefresh() }) {
                        Text(text = stringResource(screenState.errorStateValues.refreshButtonText))
                    }
                }

                is SearchScreenState.Empty -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = customAnnotatedString(screenState.emptyStateValues.noResultsText, state.query),
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onManualAdd(state.query) }) {
                        Text(text = stringResource(screenState.emptyStateValues.manualButtonText))
                    }
                }

                is SearchScreenState.Partial -> {
                    BooksList(
                        contentStateValues = screenState.contentStateValues,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        query = state.query,
                        items = screenState.items,
                        onSelect = onSelectBook,
                        canLoadMore = state.canLoadMore,
                        isLoadingMore = state.isLoadingMore,
                        onLoadMore = onLoadMore,
                        onManualAdd = onManualAdd,
                        showingPartialResults = true,
                    )
                }

                is SearchScreenState.Success -> {
                    BooksList(
                        contentStateValues = screenState.contentStateValues,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        query = state.query,
                        items = screenState.items,
                        onSelect = onSelectBook,
                        canLoadMore = state.canLoadMore,
                        isLoadingMore = state.isLoadingMore,
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
    selected: BookSearchModeUi,
    onSelect: (BookSearchModeUi) -> Unit,
) {
    val options = remember { BookSearchModeUi.allOptions() }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        options.forEach { mode ->
            FilterChip(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                label = { Text(text = mode.label.asString()) },
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
    items: List<SearchBookDataState>,
    onSelect: (SearchBookDataState) -> Unit,
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
                            url = book.url,
                            requestHeaders = book.headers,
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
                                text = book.authors,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        if (book.meta.isNotBlank()) {
                            Spacer(Modifier.height(height = 4.dp))
                            Text(
                                text = book.meta,
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
                                sourceText = book.source.label.asString(),
                                modifier = Modifier.padding(all = 2.dp),
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
