package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.LibraryFilters
import com.viroge.booksanalyzer.domain.LibrarySort
import com.viroge.booksanalyzer.ui.components.BookSourceBadge
import com.viroge.booksanalyzer.ui.components.CommonCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize
import com.viroge.booksanalyzer.ui.components.CommonItemCard
import com.viroge.booksanalyzer.ui.components.CommonLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar
import com.viroge.booksanalyzer.ui.screens.books.LibrarySortMapper
import com.viroge.booksanalyzer.ui.screens.books.StatusMapper

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
                title = stringResource(R.string.library_screen_name),
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
                    placeholder = { Text(text = stringResource(R.string.library_screen_search_placeholder)) },
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
                Spacer(Modifier.height(height = 16.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.library_screen_empty_state_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(height = 8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.library_screen_empty_state_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
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
                                text = stringResource(R.string.library_screen_currently_reading_section_title).uppercase(),
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

                        item { Spacer(modifier = Modifier.height(height = 16.dp)) }
                    }

                    // A list of all saved books
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(R.string.library_screen_all_section_title).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    items(
                        items = state.books,
                        key = { it.id },
                    ) { book ->
                        BookRowCard(
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
fun ActiveFiltersRow(
    filters: LibraryFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = buildList {
        filters.status?.let {
            StatusMapper.getUiModel(status = it).text
                .also { statusText -> add(statusText) }
        }

        if (filters.sort != LibrarySort.ADDED) {
            stringResource(R.string.library_sort_explanation_prefix, LibrarySortMapper.getUiModel(filters.sort).text)
                .also { add(it) }
        }
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
        TextButton(onClick = onClearFilters) {
            Text(text = stringResource(R.string.library_screen_filter_button_clear_label))
        }
    }
}

@Composable
fun CurrentlyReadingCard(
    book: Book,
    onClick: () -> Unit
) {
    CommonItemCard(
        modifier = Modifier.width(width = 210.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                CommonCoverAsyncImage(
                    url = book.coverUrl,
                    requestHeaders = book.coverRequestHeaders,
                    size = CommonAsyncImageSize.MEDIUM,
                    modifier = Modifier.align(Alignment.Center),
                )
                BookSourceBadge(
                    source = book.source,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(all = 2.dp),
                )
            }

            CommonLinearProgressIndicator(progress = { 0.3F })

            Text(
                text = book.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

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
private fun BookRowCard(
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
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            CommonCoverAsyncImage(
                url = book.coverUrl,
                requestHeaders = book.coverRequestHeaders,
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

                Spacer(modifier = Modifier.height(height = 8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BookStatusBadge(
                        status = book.status,
                        modifier = Modifier.padding(all = 2.dp),
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f))
                    BookSourceBadge(
                        source = book.source,
                        modifier = Modifier.padding(all = 2.dp),
                    )
                }
            }
        }
    }
}
