package com.viroge.booksanalyzer.ui.books.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.LibraryFilters
import com.viroge.booksanalyzer.domain.LibrarySort
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.ui.common.CommonAsyncImage
import com.viroge.booksanalyzer.ui.common.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.common.CommonItemCard
import com.viroge.booksanalyzer.ui.common.CommonLinearProgressIndicator
import com.viroge.booksanalyzer.ui.common.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: LibraryViewModel,
    onOpenBook: (String) -> Unit,
) {

    val state = vm.uiState.collectAsState().value
    val filters by vm.filters.collectAsState()

    var showSearch by rememberSaveable { mutableStateOf(value = false) }
    var showFilters by rememberSaveable { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentListState = rememberLazyListState()
    val currentOrderKey = remember(key1 = state.books) {
        state.currentlyReading.joinToString(separator = "|") { it.id }
    }
    LaunchedEffect(key1 = currentOrderKey) {
        currentListState.scrollToItem(index = 0)
    }

    val fullListState = rememberLazyListState()
    val fullOrderKey = remember(key1 = state.books) {
        state.books.joinToString(separator = "|") { it.id }
    }
    LaunchedEffect(key1 = fullOrderKey) {
        if (state.sort == LibrarySort.RECENT) fullListState.scrollToItem(index = 0)
    }

    if (showFilters) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showFilters = false },
            containerColor = MaterialTheme.colorScheme.surface,
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
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = "My Books",
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
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            ActiveFiltersRow(
                filters = filters,
                onClearFilters = vm::onClearFilters,
            )

            if (showSearch) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = vm::onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(text = "Search your library…") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (state.query.isBlank()) {
                                showSearch = false
                            } else {
                                vm.onQueryChange(value = "")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }

            if (state.books.isEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "No books yet.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Tap + to add your first book.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    state = fullListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                ) {
                    // Currently Reading section
                    if (state.currentlyReading.isNotEmpty()) {
                        item {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = "Currently reading".uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        item {
                            LazyRow(
                                state = currentListState,
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            ) {
                                items(
                                    items = state.currentlyReading,
                                    key = { it.id },
                                ) { book ->
                                    CurrentlyReadingCard(
                                        book = book,
                                        onClick = { onOpenBook(book.id) },
                                    )
                                }
                            }
                        }
                    }

                    // A list of all saved books
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "Collection".uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    items(
                        items = state.books,
                        key = { it.id },
                    ) { book ->
                        BookRow(
                            book = book,
                            onClick = { onOpenBook(book.id) },
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            thickness = 1.dp,
        )

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
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = "By Added to Collection",
            selected = filters.sort == LibrarySort.ADDED,
            onClick = { onSortChange(LibrarySort.ADDED) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = "By Most Recently Viewed",
            selected = filters.sort == LibrarySort.RECENT,
            onClick = { onSortChange(LibrarySort.RECENT) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = "By Title",
            selected = filters.sort == LibrarySort.TITLE,
            onClick = { onSortChange(LibrarySort.TITLE) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = "By Author",
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
fun ActiveFiltersRow(
    filters: LibraryFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = buildList {
        filters.status?.let { add("Status: ${it.name.pretty()}") }
        if (filters.sort != LibrarySort.ADDED) add("Sort: ${filters.sort.name.pretty()}")
    }

    if (parts.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
fun CurrentlyReadingCard(
    book: Book,
    onClick: () -> Unit
) {
    CommonItemCard(
        modifier = Modifier
            .heightIn(min = 350.dp)
            .width(width = 210.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            CommonAsyncImage(
                url = book.coverUrl,
                size = CommonAsyncImageSize.MEDIUM,
            )

            CommonLinearProgressIndicator(
                progress = { 0.3F },
            )

            Text(
                text = book.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.weight(weight = 1f))

            if (book.authors.isNotEmpty()) {
                Text(
                    text = book.authors.joinToString(separator = ", "),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}


@Composable
private fun BookRow(
    book: Book,
    onClick: () -> Unit,
) {
    CommonItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(all = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            CommonAsyncImage(
                url = book.coverUrl,
                size = CommonAsyncImageSize.SMALL,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                val meta = listOfNotNull(
                    book.publishedYear?.toString(),
                    book.isbn13
                ).joinToString(separator = " • ")

                if (meta.isNotBlank()) {
                    Spacer(Modifier.height(height = 4.dp))
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = book.status.name.pretty(),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                )
            }
        }
    }
}
