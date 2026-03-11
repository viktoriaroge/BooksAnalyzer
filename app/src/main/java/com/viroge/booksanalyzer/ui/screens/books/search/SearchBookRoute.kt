package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchBookRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onGoToConfirm: () -> Unit,
) {

    val vm: SearchBookViewModel = hiltViewModel()
    val mode by vm.modeState.collectAsState()

    BookSearchScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        vm = vm,
        onLoadMore = { vm.loadMore() },
        onQueryChanged = { vm.changeQuery(newValue = it) },
        onModeChanged = { vm.changeSearchMode(newMode = it) },
        onSelectBook = { book ->
            vm.selectBook(book)
            onGoToConfirm()
        },
        onRefresh = { vm.refresh() },
        onManualAdd = { prefill ->
            vm.setManualPrefill(prefill, mode)
            onGoToConfirm()
        },
    )
}
