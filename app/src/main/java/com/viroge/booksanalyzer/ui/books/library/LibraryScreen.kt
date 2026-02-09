package com.viroge.booksanalyzer.ui.books.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            TopAppBar(title = { Text("Library") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) { Text("+") }
        },
    ) { padding ->

        if (books.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No books yet.",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(8.dp))

                Text(text = "Tap + to add your first book.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = books,
                    key = { it.bookId },
                ) { book ->
                    BookRow(book = book, onClick = { onOpenBook(book.bookId) })
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

        Column(
            Modifier.padding(12.dp),
        ) {

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
            )

            if (book.authors.isNotBlank()) {
                Text(
                    text = book.authors,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            val meta = listOfNotNull(
                book.publishedYear?.toString(),
                book.isbn13
            ).joinToString(" • ")

            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
