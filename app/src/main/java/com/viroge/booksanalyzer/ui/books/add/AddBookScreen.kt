package com.viroge.booksanalyzer.ui.books.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.InputChip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.SearchMode
import com.viroge.booksanalyzer.ui.common.BookSourceBadge
import com.viroge.booksanalyzer.ui.common.BookStatusBadge
import com.viroge.booksanalyzer.ui.common.CommonAsyncImage
import com.viroge.booksanalyzer.ui.common.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.common.CommonItemCard
import com.viroge.booksanalyzer.ui.common.CommonLinearProgressIndicator
import com.viroge.booksanalyzer.ui.common.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    vm: SearchBookViewModel,
    onLoadMore: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (SearchMode) -> Unit,
    onSelectCandidate: (BookCandidate) -> Unit,
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
        topBar = { CommonTopAppBar(title = "Find Books") },
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
                label = { Text(text = "Search by title, author, or ISBN") },
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
                    CommonLinearProgressIndicator()
                }

                is SearchUiState.Error -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = selectedState.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                is SearchUiState.Empty -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "No results for “${selectedState.query}”.",
                    )

                    Spacer(Modifier.height(height = 8.dp))
                    Button(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onManualAdd(selectedState.query) }) {
                        Text(text = "Add manually")
                    }
                }

                is SearchUiState.Partial -> {
                    Spacer(Modifier.height(height = 16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Some sources failed. Showing available results. Not in the list?",
                    )

                    Spacer(Modifier.height(height = 8.dp))

                    Button(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onManualAdd(selectedState.query) },
                    ) {
                        Text(text = "Add manually")
                    }

                    CandidatesList(
                        selectedState.query,
                        selectedState.items,
                        onSelectCandidate,
                        canLoadMore,
                        isLoadingMore,
                        onLoadMore,
                        onManualAdd,
                    )
                }

                is SearchUiState.Success -> {
                    CandidatesList(
                        selectedState.query,
                        selectedState.items,
                        onSelectCandidate,
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
                        Text(text = "Clear search history?")
                    },
                    text = {
                        Text(text = "This will remove all previous search attempts. This can't be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                                vm.clearRecents()
                            }) { Text(text = "Clear") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                            }) { Text(text = "Cancel") }
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
        FilterChip(
            selected = selected == SearchMode.ALL,
            onClick = { onSelect(SearchMode.ALL) },
            label = { Text(text = "All") },
        )
        FilterChip(
            selected = selected == SearchMode.TITLE,
            onClick = { onSelect(SearchMode.TITLE) },
            label = { Text(text = "Title") },
        )
        FilterChip(
            selected = selected == SearchMode.AUTHOR,
            onClick = { onSelect(SearchMode.AUTHOR) },
            label = { Text(text = "Author") },
        )
        FilterChip(
            selected = selected == SearchMode.ISBN,
            onClick = { onSelect(SearchMode.ISBN) },
            label = { Text(text = "ISBN") },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentSearchesSection(
    recent: List<String>,
    onPick: (String) -> Unit,
    onDeleteOne: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recent.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = "Recent searches",
                style = MaterialTheme.typography.titleSmall,
            )

            TextButton(onClick = onClearAll) { Text(text = "Clear all") }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        ) {

            recent.forEach { q ->
                RecentQueryChip(
                    query = q,
                    onPick = { onPick(q) },
                    onDelete = { onDeleteOne(q) },
                )
            }
        }
    }
}

@Composable
private fun RecentQueryChip(
    query: String,
    onPick: () -> Unit,
    onDelete: () -> Unit,
) {
    InputChip(
        selected = false,
        onClick = onPick,
        label = {
            Row(
                modifier = Modifier.heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = query,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun CandidatesList(
    query: String,
    items: List<BookCandidate>,
    onSelect: (BookCandidate) -> Unit,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onManualAdd: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {
        items(items) { candidate ->
            CommonItemCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelect(candidate) },
            ) {
                Row(
                    modifier = Modifier.padding(all = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                ) {
                    CommonAsyncImage(
                        url = candidate.coverUrl,
                        size = CommonAsyncImageSize.XSMALL,
                    )

                    Column(modifier = Modifier.weight(weight = 1f)) {

                        Text(
                            text = candidate.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (candidate.authors.isNotEmpty()) {
                            Spacer(Modifier.height(height = 4.dp))
                            Text(
                                text = candidate.authors.joinToString(separator = ", "),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        val meta = listOfNotNull(
                            candidate.publishedYear?.toString(),
                            candidate.isbn13,
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
                                source = candidate.source,
                                modifier = Modifier.padding(all = 2.dp),
                                showFullSourceName = true,
                            )
                        }
                    }
                }
            }
        }
        if (canLoadMore) {
            item {
                Spacer(Modifier.height(height = 8.dp))

                Button(
                    onClick = { onLoadMore() },
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isLoadingMore) "Loading…" else "Show more")
                }
            }
        } else {
            item {
                Spacer(Modifier.height(height = 8.dp))
                Text(text = "Not in the list?")
                Spacer(Modifier.height(height = 8.dp))

                Button(onClick = { onManualAdd(query) }) {
                    Text(text = "Add manually")
                }
            }
        }
    }
}
