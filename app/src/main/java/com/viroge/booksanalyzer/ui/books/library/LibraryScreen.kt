package com.viroge.booksanalyzer.ui.books.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.domain.LibraryFilters
import com.viroge.booksanalyzer.domain.LibrarySort
import com.viroge.booksanalyzer.domain.ReadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: LibraryViewModel,
    onAddBook: () -> Unit,
    onOpenBook: (String) -> Unit,
) {

    val state = vm.uiState.collectAsState().value
    val filters by vm.filters.collectAsState()

    var showSearch by rememberSaveable { mutableStateOf(value = false) }
    var showFilters by rememberSaveable { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showFilters) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showFilters = false }
        ) {
            LibraryFiltersSheet(
                filters = filters,
                onStatusChange = vm::onStatusChange,
                onSortChange = vm::onSortChange,
                onClear = vm::onClearFilters,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Books") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) { Text(text = "+") }
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(paddingValues = padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {

            if (showSearch) {
                LibrarySearchField(
                    query = state.query,
                    onQueryChange = vm::onQueryChange,
                    onClear = {
                        showSearch = false
                        vm.onQueryChange(value = "")
                    },
                )

                Spacer(Modifier.height(height = 8.dp))
            }

            ActiveFiltersRow(
                filters = filters,
                onClearFilters = vm::onClearFilters,
                modifier = Modifier.padding(vertical = 0.dp),
            )

            if (state.books.isEmpty()) {
                Text(
                    text = "No books yet.",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(Modifier.height(height = 8.dp))

                Text(text = "Tap + to add your first book.")
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                ) {
                    // Currently Reading section
                    if (state.currentlyReading.isNotEmpty()) {
                        item {
                            CurrentlyReadingSection(
                                books = state.currentlyReading,
                                onOpenBook = onOpenBook,
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Your collection".uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }

                    // A list of all saved books
                    items(
                        items = state.books,
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
}

@Composable
fun LibraryFiltersSheet(
    filters: LibraryFilters,
    onStatusChange: (ReadingStatus?) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Filters", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClear) { Text(text = "Clear") }
        }

        Text(text = "Status", style = MaterialTheme.typography.titleSmall)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            FilterChip(
                selected = filters.status == null,
                onClick = { onStatusChange(null) },
                label = { Text(text = "All") }
            )

            ReadingStatus.entries.forEach { status ->
                FilterChip(
                    selected = filters.status == status,
                    onClick = { onStatusChange(status) },
                    label = { Text(text = status.name.pretty()) }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            thickness = 1.dp,
        )

        Text(
            text = "Sort",
            style = MaterialTheme.typography.titleSmall,
        )

        SortRadioRow(
            label = "Recent",
            selected = filters.sort == LibrarySort.RECENT,
            onClick = { onSortChange(LibrarySort.RECENT) },
        )
        SortRadioRow(
            label = "Title",
            selected = filters.sort == LibrarySort.TITLE,
            onClick = { onSortChange(LibrarySort.TITLE) },
        )
        SortRadioRow(
            label = "Author",
            selected = filters.sort == LibrarySort.AUTHOR,
            onClick = { onSortChange(LibrarySort.AUTHOR) },
        )
    }
}

@Composable
private fun SortRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
fun LibrarySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(text = "Search your library…") },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) { Text(text = "✕") }
            }
        },
    )
}

@Composable
fun ActiveFiltersRow(
    filters: LibraryFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = buildList {
        filters.status?.let { add(it.name.pretty()) }
        if (filters.sort != LibrarySort.RECENT) add("Sort: ${filters.sort.name.pretty()}")
    }

    if (parts.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = parts.joinToString(separator = " • "),
            style = MaterialTheme.typography.bodySmall
        )
        TextButton(onClick = onClearFilters) { Text(text = "Clear") }
    }
}

private fun String.pretty(): String = lowercase()
    .replace(oldChar = '_', newChar = ' ')
    .replaceFirstChar { it.uppercase() }

@Composable
fun CurrentlyReadingSection(
    books: List<BookEntity>,
    onOpenBook: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 8.dp)) {
        Text(
            text = "Currently reading".uppercase(),
            style = MaterialTheme.typography.labelSmall,
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(space = 12.dp)) {
            items(items = books, key = { it.bookId }) { book ->

                CurrentlyReadingCard(
                    book = book,
                    onClick = { onOpenBook(book.bookId) },
                )
            }
        }
    }
}

@Composable
fun CurrentlyReadingCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(width = 260.dp),
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = book.coverUrl)
                    .crossfade(enable = true)
                    .build(),
                error = painterResource(id = R.drawable.blank_book),
                contentDescription = null,
                modifier = Modifier.size(width = 56.dp, height = 84.dp),
            )

            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                )

                if (book.authors.isNotBlank()) {
                    Text(
                        text = book.authors,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
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
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = book.coverUrl)
                    .crossfade(enable = true)
                    .build(),
                error = painterResource(id = R.drawable.blank_book),
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

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = book.status.replace(oldChar = '_', newChar = ' '),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                )
            }
        }
    }
}
