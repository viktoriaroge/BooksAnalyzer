package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchBookRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onGoToConfirm: () -> Unit,
) {
    val vm: BookSearchViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    var confirmClear by remember { mutableStateOf(value = false) }

    BookSearchScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        state = state,
        onLoadMore = vm::loadMore,
        onRefresh = vm::refreshSearch,
        onQueryChanged = vm::changeQuery,
        onModeChanged = vm::changeSearchMode,
        onRecentSearchSelected = vm::selectRecent,
        onRemoveRecentSearch = vm::removeRecent,
        onClearRecentSearches = { confirmClear = true },
        onManualAdd = { prefill ->
            vm.setManualPrefill(prefill, state.mode)
            onGoToConfirm()
        },
        onSelectBook = { book ->
            vm.selectBook(book)
            onGoToConfirm()
        },
    )

    when (val screenState = state.screenState) {
        is SearchScreenState.Idle -> {
            if (confirmClear) {
                AlertDialog(
                    onDismissRequest = { confirmClear = false },
                    title = { Text(text = stringResource(screenState.searchHistoryDialogValues.title)) },
                    text = { Text(text = stringResource(screenState.searchHistoryDialogValues.text)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                                vm.clearRecents()
                            })
                        { Text(text = stringResource(screenState.searchHistoryDialogValues.clearButtonText)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { confirmClear = false })
                        { Text(text = stringResource(screenState.searchHistoryDialogValues.cancelButtonText)) }
                    },
                )
            }
        }

        else -> {}
    }
}
