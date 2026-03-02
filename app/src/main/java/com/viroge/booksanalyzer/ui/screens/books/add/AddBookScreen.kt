package com.viroge.booksanalyzer.ui.screens.books.add

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    vm: SearchBookViewModel,
    onLoadMore: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (SearchMode) -> Unit,
    onSelectBook: (Book) -> Unit,
    onRefresh: () -> Unit,
    onManualAdd: (String) -> Unit,
) {

    val query by vm.queryState.collectAsState()

    val recent by vm.recentQueries.collectAsState()
    var confirmClear by remember { mutableStateOf(value = false) }

    val canLoadMore by vm.canLoadMore.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val mode by vm.modeState.collectAsState()
    val state by vm.uiState.collectAsState()

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
                    IconButton(onClick = { vm.changeQuery(newValue = "") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }
                }
            )

            when (val selectedState = state) {
                SearchUiState.Idle -> {
                    RecentSearchesSection(
                        recent = recent,
                        onPick = { picked -> vm.changeQuery(newValue = picked) },
                        onDeleteOne = vm::removeRecent,
                        onClearAll = { confirmClear = true },
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
                        text = selectedState.message,
                        color = MaterialTheme.colorScheme.error,
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onRefresh() }) {
                        Text(text = stringResource(R.string.search_screen_refresh_button))
                    }
                }

                is SearchUiState.Empty -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = customAnnotatedString(R.string.search_screen_no_results_error_text, selectedState.query),
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { onManualAdd(selectedState.query) }) {
                        Text(text = stringResource(R.string.search_screen_add_manually_button))
                    }
                }

                is SearchUiState.Partial -> {
                    BooksList(
                        selectedState.query,
                        selectedState.items,
                        onSelectBook,
                        canLoadMore,
                        isLoadingMore,
                        onLoadMore,
                        onManualAdd,
                        showingPartialResults = true,
                    )
                }

                is SearchUiState.Success -> {
                    BooksList(
                        selectedState.query,
                        selectedState.items,
                        onSelectBook,
                        canLoadMore,
                        isLoadingMore,
                        onLoadMore,
                        onManualAdd,
                    )
                }
            }

            if (confirmClear) {
                AlertDialog(
                    onDismissRequest = { confirmClear = false },
                    title = {
                        Text(text = stringResource(R.string.search_screen_clear_history_dialog_title))
                    },
                    text = {
                        Text(text = stringResource(R.string.search_screen_clear_history_dialog_text))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                                vm.clearRecents()
                            }) { Text(text = stringResource(R.string.search_screen_clear_history_clear_button)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                            }) { Text(text = stringResource(R.string.search_screen_clear_history_cancel_button)) }
                    },
                )
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
                    PvBookCoverAsyncImage(
                        url = book.coverUrl,
                        requestHeaders = book.coverRequestHeaders,
                        size = PvBookCoverImageSize.XSMALL,
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
                                text = stringResource(R.string.search_screen_source_label),
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
                Text(text = stringResource(R.string.search_screen_partial_results_error_text))
            }

            Spacer(Modifier.height(height = 8.dp))
            Text(text = stringResource(R.string.search_screen_load_more_suggestion_text))

            if (canLoadMore) {
                Spacer(Modifier.height(height = 8.dp))
                Button(
                    onClick = { onLoadMore() },
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text =
                            if (isLoadingMore) stringResource(R.string.search_screen_load_more_button_in_progress_text)
                            else stringResource(R.string.search_screen_load_more_button_default_text)
                    )
                }
            }

            Spacer(Modifier.height(height = 8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onManualAdd(query) },
            ) {
                Text(text = stringResource(R.string.search_screen_add_manually_button))
            }

            Spacer(Modifier.height(height = 16.dp))
        }
    }
}
