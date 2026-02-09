package com.viroge.booksanalyzer.ui.books.library

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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.viroge.booksanalyzer.data.local.BookEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    books: List<BookEntity>,
    onAddBook: () -> Unit,
    onOpenBook: (String) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Library") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) { Text(text = "+") }
        },
    ) { padding ->

        if (books.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues = padding)
                    .fillMaxSize()
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No books yet.",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(height = 8.dp))

                Text(text = "Tap + to add your first book.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues = padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            ) {
                items(
                    items = books,
                    key = { it.bookId },
                ) { book ->

                    BookRow(
                        book = book,
                        onClick = { onOpenBook(book.bookId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BookRow(
    book: BookEntity,
    onClick: () -> Unit,
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {

        Row(
            modifier = Modifier.padding(all = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {

            AsyncImage(
                model = book.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 80.dp, height = 120.dp),
            )

            Column(modifier = Modifier.weight(weight = 1f)) {

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                )

                if (book.authors.isNotBlank()) {
                    Text(text = book.authors)
                }

                val meta = listOfNotNull(
                    book.publishedYear?.toString(),
                    book.isbn13
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
