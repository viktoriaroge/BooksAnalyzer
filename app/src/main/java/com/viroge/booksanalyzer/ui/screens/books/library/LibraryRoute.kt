package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.viroge.booksanalyzer.domain.model.BookSeed

@Composable
fun LibraryRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onOpenSearch: () -> Unit,
    onOpenCollection: () -> Unit,
    onOpenBook: (BookSeed) -> Unit,
) {
    val vm: LibraryViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(vm.events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.events.collect { event ->
                when (event) {
                    is LibraryEvent.OpenBookDetails -> {
                        onOpenBook(event.seed)
                    }
                }
            }
        }
    }

    when (val screenState = state.screenState) {
        LibraryScreenState.Loading -> {
            // Draw nothing and have a smooth transition without anything flickering.
            // The DB fetch is quick enough. If needed, can be implemented later.
        }

        is LibraryScreenState.Empty -> {
            LibraryEmptyScreen(
                screenValues = state.screenValues,
                emptyStateValues = screenState.emptyStateValues,
                actionIcon = when (screenState.navRoute) {
                    LibraryNavDirection.SEARCH -> Icons.Default.Search
                    LibraryNavDirection.COLLECTION -> Icons.Default.LocalLibrary
                },
                onAction = {
                    when (screenState.navRoute) {
                        LibraryNavDirection.SEARCH -> onOpenSearch()
                        LibraryNavDirection.COLLECTION -> onOpenCollection()
                    }
                },
            )
        }

        is LibraryScreenState.Content -> {
            val pagerState = rememberPagerState(
                pageCount = { screenState.currentBooks.size }
            )
            val currentOrderKey = remember(screenState.currentBooks) {
                screenState.currentBooks.joinToString(separator = "|") { it.id }
            }
            LaunchedEffect(currentOrderKey) {
                if (screenState.currentBooks.isNotEmpty()) {
                    pagerState.scrollToPage(0)
                }
            }
            val activeBook = remember(pagerState.currentPage, screenState.currentBooks) {
                screenState.currentBooks.getOrNull(pagerState.currentPage)
            }
            LibraryScreen(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = screenState,
                activeBook = activeBook,
                values = screenState.contentStateValues,
                pagerState = pagerState,
                onOpenCollection = onOpenCollection,
                onOpenBook = vm::onOpenBook,
            )
        }
    }
}
