package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryRoute(
    onOpenBook: () -> Unit,
) {

    val vm: LibraryViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val filters by vm.filters.collectAsState()
    val query by vm.query.collectAsState()

    var showSearch by rememberSaveable { mutableStateOf(value = false) }
    var showFilters by rememberSaveable { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentListState = rememberLazyListState()
    val currentOrderKey = remember(key1 = state.allBooks) {
        state.currentBooks.joinToString(separator = "|") { it.id }
    }
    LaunchedEffect(key1 = currentOrderKey) {
        currentListState.scrollToItem(index = 0)
    }

    val fullListState = rememberLazyListState()
    val fullOrderKey = remember(key1 = state.allBooks) {
        state.allBooks.joinToString(separator = "|") { it.id }
    }
    LaunchedEffect(key1 = fullOrderKey) {
        if (state.sortState == LibrarySortUi.Recent) fullListState.scrollToItem(index = 0)
    }

    LibraryScreen(
        state = state,
        filters = filters,
        query = query,
        currentListState = currentListState,
        fullListState = fullListState,
        showSearch = showSearch,
        onToggleSearch = { showSearch = !showSearch },
        onHideSearch = { showSearch = false },
        onToggleFilters = { showFilters = !showFilters },
        onClearFilters = vm::onClearFilters,
        onQueryChange = vm::onQueryChange,
        onOpenBook = { bookId ->
            vm.selectBook(bookId)
            onOpenBook()
        },
    )

    if (showFilters) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showFilters = false },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            LibraryFiltersSheet(
                screenValues = state.screenValues,
                filters = filters,
                onStatusChange = vm::onStatusChange,
                onSortChange = vm::onSortChange,
                onClear = vm::onClearFilters,
            )
        }
    }
}
