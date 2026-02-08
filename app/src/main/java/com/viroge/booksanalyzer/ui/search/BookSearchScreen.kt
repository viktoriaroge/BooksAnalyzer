package com.viroge.booksanalyzer.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.BookCandidate

@Composable
fun BookSearchScreen(
    vm: BookSearchViewModel,
    onSelectCandidate: (BookCandidate) -> Unit,
    onManualAdd: (String) -> Unit,
) {

    val query by vm.queryState.collectAsState()
    val state by vm.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = vm::onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search by title, author, or ISBN") },
            singleLine = true,
        )

        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            SearchUiState.Idle -> {
                Text("Type to search…", style = MaterialTheme.typography.bodyMedium)
            }

            SearchUiState.Loading -> {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            is SearchUiState.Error -> {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }

            is SearchUiState.Empty -> {
                Text("No results for “${s.query}”.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onManualAdd(s.query) }) {
                    Text("Add manually")
                }
            }

            is SearchUiState.Partial -> {
                // Results + non-blocking warning
                Text(
                    "Some sources failed. Showing available results.",
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(8.dp))
                CandidatesList(s.items, onSelectCandidate)
            }

            is SearchUiState.Success -> {
                CandidatesList(s.items, onSelectCandidate)
            }
        }
    }
}

@Composable
private fun CandidatesList(
    items: List<BookCandidate>,
    onSelect: (BookCandidate) -> Unit,
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { candidate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelect(candidate) },
            ) {
                Column(Modifier.padding(12.dp)) {

                    Text(candidate.title, style = MaterialTheme.typography.titleMedium)

                    if (candidate.authors.isNotEmpty()) {
                        Text(candidate.authors.joinToString(", "))
                    }

                    val meta = listOfNotNull(
                        candidate.publishedYear?.toString(),
                        candidate.isbn13,
                    ).joinToString(" • ")

                    if (meta.isNotBlank()) {
                        Text(meta, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
