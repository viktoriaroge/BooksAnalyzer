package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.pager.rememberPagerState
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
