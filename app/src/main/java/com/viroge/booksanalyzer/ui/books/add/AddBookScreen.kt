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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    vm: SearchBookViewModel,
    onSelectCandidate: (BookCandidate) -> Unit,
    onManualAdd: (String) -> Unit,
    onBack: () -> Unit,
) {

    val query by vm.queryState.collectAsState()
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
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Search by title, author, or ISBN") },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))

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

                    CandidatesList(selectedState.items, onSelectCandidate)
                }

                is SearchUiState.Success -> {
                    CandidatesList(selectedState.items, onSelectCandidate)
                }
            }
        }
    }
}

@Composable
private fun CandidatesList(
    items: List<BookCandidate>,
    onSelect: (BookCandidate) -> Unit,
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
    }
}
