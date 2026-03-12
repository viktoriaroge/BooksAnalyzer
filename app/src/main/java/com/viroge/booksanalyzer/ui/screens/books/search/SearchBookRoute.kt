package com.viroge.booksanalyzer.ui.screens.books.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchBookRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onGoToConfirm: () -> Unit,
) {

    val vm: SearchBookViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val mode by vm.modeState.collectAsState()
    val query by vm.queryState.collectAsState()
    val recent by vm.recentQueries.collectAsState()
    val canLoadMore by vm.canLoadMore.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()

    var confirmClear by remember { mutableStateOf(value = false) }

    BookSearchScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        state = state,
        query = query,
        mode = mode,
        recent = recent,
        canLoadMore = canLoadMore,
        isLoadingMore = isLoadingMore,
        onLoadMore = vm::loadMore,
        onRefresh = vm::refresh,
        onQueryChanged = vm::changeQuery,
        onModeChanged = vm::changeSearchMode,
        onRemoveRecentSearch = vm::removeRecent,
        onClearRecentSearches = { confirmClear = true },
        onManualAdd = { prefill ->
            vm.setManualPrefill(prefill, mode)
            onGoToConfirm()
        },
        onSelectBook = { book ->
            vm.selectBook(book)
            onGoToConfirm()
        },
    )

    when (val selectedState = state) {
        is SearchUiState.Idle -> {
            if (confirmClear) {
                AlertDialog(
                    onDismissRequest = { confirmClear = false },
                    title = {
                        Text(text = stringResource(selectedState.searchHistoryDialogValues.title))
                    },
                    text = {
                        Text(text = stringResource(selectedState.searchHistoryDialogValues.text))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                                vm.clearRecents()
                            }) { Text(text = stringResource(selectedState.searchHistoryDialogValues.clearButtonText)) }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                confirmClear = false
                            }) { Text(text = stringResource(selectedState.searchHistoryDialogValues.cancelButtonText)) }
                    },
                )
            }
        }

        else -> {}
    }
}
