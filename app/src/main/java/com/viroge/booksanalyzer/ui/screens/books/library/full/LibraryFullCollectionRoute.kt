package com.viroge.booksanalyzer.ui.screens.books.library.full

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryFullCollectionRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
    onOpenBook: () -> Unit,
) {

    val vm: LibraryFullCollectionViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    when (val screenState = state.screenState) {
        LibraryFullCollectionScreenState.Loading -> {
            // Draw nothing and have a smooth transition without anything flickering.
            // The DB fetch is quick enough. If needed, can be implemented later.
        }

        is LibraryFullCollectionScreenState.Content -> {
            val filters by vm.filters.collectAsState()
            val query by vm.query.collectAsState()

            var showSearch by rememberSaveable { mutableStateOf(value = false) }
            var showFilters by rememberSaveable { mutableStateOf(value = false) }

            val fullListState = rememberLazyListState()
            val fullOrderKey = remember(key1 = screenState.allBooks) {
                screenState.allBooks.joinToString(separator = "|") { it.id }
            }
            LaunchedEffect(key1 = fullOrderKey) {
                if (screenState.sortState == LibrarySortUi.Recent) fullListState.scrollToItem(index = 0)
            }
            LibraryFullCollectionScreen(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = screenState,
                screenValues = state.screenValues,
                values = screenState.fullCollectionStateValues,
                filters = filters,
                query = query,
                fullListState = fullListState,
                showSearch = showSearch,
                onToggleSearch = remember { { showSearch = !showSearch } },
                onHideSearch = remember { { showSearch = false } },
                onToggleFilters = remember { { showFilters = !showFilters } },
                onClearFilters = vm::onClearFilters,
                onQueryChange = vm::onQueryChange,
                onToggleLibraryView = onBack,
                onOpenBook = remember {
                    { bookId ->
                        vm.selectBook(bookId)
                        onOpenBook()
                    }
                },
            )

            if (showFilters) {
                LibraryFiltersSheet(
                    sheetValues = screenState.filtersSheetValues,
                    filters = filters,
                    onStatusChange = vm::onStatusChange,
                    onSortChange = vm::onSortChange,
                    onClear = vm::onClearFilters,
                    onDismiss = remember { { showFilters = false } },
                )
            }
        }
    }
}
