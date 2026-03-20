package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvButtonType
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

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
    onRecentSearchSelected: (String) -> Unit,
    onRemoveRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onManualAdd: (String) -> Unit,
    onSelectBook: (SearchBookDataState) -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = { PvTopAppBar(title = stringResource(state.screenValues.screenName)) },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
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
                    IconButton(onClick = remember { { onQueryChanged("") } }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = when (state.mode) {
                        BookSearchModeUi.All,
                        BookSearchModeUi.Author,
                        BookSearchModeUi.Title -> KeyboardType.Text

                        BookSearchModeUi.Isbn -> KeyboardType.Number
                    }
                ),
                keyboardActions = KeyboardActions(
                    onSearch = remember {
                        {
                            focusManager.clearFocus()
                            onRefresh()
                        }
                    }
                ),
            )

            when (val screenState = state.screenState) {
                SearchScreenState.Loading -> {
                    Spacer(Modifier.height(height = 4.dp))
                    PvLinearProgressIndicator()
                }

                is SearchScreenState.Idle -> {
                    RecentSearchesSection(
                        values = screenState.recentSearchesValues,
                        recent = state.recent,
                        onPick = onRecentSearchSelected,
                        onDeleteOne = onRemoveRecentSearch,
                        onClearAll = onClearRecentSearches,
                    )
                }

                is SearchScreenState.Empty -> {
                    EmptyState(
                        state = state,
                        values = screenState.emptyStateValues,
                        onManualAdd = onManualAdd,
                    )
                }

                is SearchScreenState.Error -> {
                    ErrorState(
                        values = screenState.errorStateValues,
                        onRefresh = onRefresh,
                    )
                }

                is SearchScreenState.Content -> {
                    BooksList(
                        contentStateValues = screenState.contentStateValues,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        query = state.query,
                        items = screenState.items,
                        onSelect = remember {
                            { book ->
                                focusManager.clearFocus()
                                onSelectBook(book)
                            }
                        },
                        canLoadMore = state.canLoadMore,
                        isLoadingMore = state.isLoadingMore,
                        onLoadMore = onLoadMore,
                        onManualAdd = onManualAdd,

                        showErrorMessage = screenState.showError,
                        errorStateValues = screenState.errorStateValues,
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
                onClick = remember { { onSelect(mode) } },
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

    showErrorMessage: Boolean,
    errorStateValues: ErrorStateValues,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        items(
            items = items,
            key = { book -> book.animationKey }, // Ensure stability during scrolls/updates, the animationKey is unique
            contentType = { "book_item" },
        ) { book ->
            PvItemCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = remember { { onSelect(book) } },
            ) {
                Row(
                    modifier = Modifier.padding(all = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                ) {
                    PvBookCoverAsyncImage(
                        url = book.url,
                        imageSize = PvBookCoverImageSize.XSmall,
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

        item(
            key = "footer",
            contentType = { "footer" },
        ) {
            if (showErrorMessage) {
                Spacer(Modifier.height(height = 8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = customAnnotatedString(errorStateValues.errorStateText.asString()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(height = 24.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(contentStateValues.additionalSuggestionText)
            )

            if (canLoadMore) {
                Spacer(Modifier.height(24.dp))
                PvButton(
                    buttonType = PvButtonType.Secondary,
                    text =
                        if (isLoadingMore) stringResource(contentStateValues.loadMoreInProgressButtonText)
                        else stringResource(contentStateValues.loadMoreDefaultButtonText),
                    icon = Icons.Default.Download,
                    enabled = !isLoadingMore,
                    onClick = onLoadMore,
                )
            }

            Spacer(Modifier.height(24.dp))
            PvButton(
                text = stringResource(contentStateValues.manualButtonText),
                icon = Icons.Default.EditNote,
                onClick = remember { { onManualAdd(query) } },
            )

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}

@Composable
private fun EmptyState(
    state: BookSearchUiState,
    values: EmptyStateValues,
    onManualAdd: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(height = 16.dp))
        Image(
            modifier = Modifier
                .size(170.dp)
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
            text = customAnnotatedString(values.emptyStateText, state.query.trim()),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(24.dp))
        PvButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(values.emptyStateButton),
            icon = Icons.Default.EditNote,
            onClick = remember { { onManualAdd(state.query) } },
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ErrorState(
    values: ErrorStateValues,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(height = 16.dp))
        Image(
            modifier = Modifier
                .size(170.dp)
                .padding(horizontal = 24.dp),
            painter = painterResource(R.drawable.ic_default_book),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(values.errorStateTitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(height = 24.dp))
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = customAnnotatedString(values.errorStateText.asString()),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(24.dp))
        PvButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            buttonType = PvButtonType.Error,
            text = stringResource(values.errorStateButton),
            onClick = onRefresh,
        )

        Spacer(Modifier.height(24.dp))
    }
}
