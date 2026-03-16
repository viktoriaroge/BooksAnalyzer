package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onOpenFullLibrary: () -> Unit,
    onOpenBook: () -> Unit,
) {

    val vm: LibraryViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    when (val screenState = state.screenState) {
        LibraryScreenState.Loading -> {
            // Draw nothing and have a smooth transition without anything flickering.
            // The DB fetch is quick enough. If needed, can be implemented later.
        }

        is LibraryScreenState.Empty -> {
            LibraryEmptyScreen(
                screenValues = state.screenValues,
                emptyStateValues = screenState.emptyStateValues,
            )
        }

        is LibraryScreenState.Content -> {
            val currentListState = rememberLazyListState()
            val currentOrderKey = remember(key1 = screenState.currentBooks) {
                screenState.currentBooks.joinToString(separator = "|") { it.id }
            }
            LaunchedEffect(key1 = currentOrderKey) {
                currentListState.scrollToItem(index = 0)
            }
            LibraryScreen(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = screenState,
                screenValues = state.screenValues,
                values = screenState.contentStateValues,
                currentListState = currentListState,
                onToggleLibraryView = onOpenFullLibrary,
                onOpenBook = remember {
                    { bookId ->
                        vm.selectBook(bookId)
                        onOpenBook()
                    }
                },
            )
        }
    }
}
