package com.viroge.booksanalyzer.ui.books.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.SearchMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    vm: SearchBookViewModel,
    onLoadMore: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (SearchMode) -> Unit,
    onSelectCandidate: (BookCandidate) -> Unit,
    onManualAdd: (String) -> Unit,
    onBack: () -> Unit,
) {

    val query by vm.queryState.collectAsState()
    val canLoadMore by vm.canLoadMore.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val mode by vm.modeState.collectAsState()
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Book Search")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) { Text(text = "←") }
                },
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(all = 16.dp),
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Search by title, author, or ISBN") },
                singleLine = true,
            )

            Spacer(Modifier.height(height = 8.dp))

            SearchModeChips(
                selected = mode,
                onSelect = { onModeChanged(it) }
            )

            Spacer(Modifier.height(height = 12.dp))

            when (val selectedState = state) {
                SearchUiState.Idle -> {
                    Text(
                        text = "Type to search…",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                SearchUiState.Loading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }

                is SearchUiState.Error -> {
                    Text(
                        text = selectedState.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                is SearchUiState.Empty -> {
                    Text(text = "No results for “${selectedState.query}”.")

                    Spacer(Modifier.height(height = 8.dp))

                    Button(onClick = { onManualAdd(selectedState.query) }) {
                        Text(text = "Add manually")
                    }
                }

                is SearchUiState.Partial -> {
                    // Results + non-blocking warning
                    Text(
                        text = "Some sources failed. Showing available results.",
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(Modifier.height(height = 8.dp))

                    CandidatesList(
                        selectedState.items,
                        onSelectCandidate,
                        canLoadMore,
                        isLoadingMore,
                        onLoadMore,
                    )
                }

                is SearchUiState.Success -> {
                    CandidatesList(
                        selectedState.items,
                        onSelectCandidate,
                        canLoadMore,
                        isLoadingMore,
                        onLoadMore
                    )
                }
            }
        }
    }
}

@Composable
fun SearchModeChips(
    selected: SearchMode,
    onSelect: (SearchMode) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == SearchMode.ALL,
            onClick = { onSelect(SearchMode.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = selected == SearchMode.TITLE,
            onClick = { onSelect(SearchMode.TITLE) },
            label = { Text("Title") }
        )
        FilterChip(
            selected = selected == SearchMode.AUTHOR,
            onClick = { onSelect(SearchMode.AUTHOR) },
            label = { Text("Author") }
        )
        FilterChip(
            selected = selected == SearchMode.ISBN,
            onClick = { onSelect(SearchMode.ISBN) },
            label = { Text("ISBN") }
        )
    }
}

@Composable
private fun CandidatesList(
    items: List<BookCandidate>,
    onSelect: (BookCandidate) -> Unit,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
        items(items) { candidate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelect(candidate) },
            ) {

                Row(
                    modifier = Modifier.padding(all = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                ) {

                    AsyncImage(
                        model = candidate.coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 80.dp, height = 120.dp),
                    )

                    Column(modifier = Modifier.weight(weight = 1f)) {

                        Text(
                            text = candidate.title,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        if (candidate.authors.isNotEmpty()) {
                            Text(text = candidate.authors.joinToString(separator = ", "))
                        }

                        val meta = listOfNotNull(
                            candidate.publishedYear?.toString(),
                            candidate.isbn13,
                        ).joinToString(separator = " • ")

                        if (meta.isNotBlank()) {
                            Text(
                                text = meta,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
        if (canLoadMore) {
            item {
                Spacer(Modifier.height(height = 12.dp))

                Button(
                    onClick = { onLoadMore() },
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isLoadingMore) "Loading…" else "Show more")
                }
            }
        }
    }
}
