package com.viroge.booksanalyzer.ui.screens.books.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddBookRoute(
    onGoToConfirm: () -> Unit,
) {

    val searchVm: SearchBookViewModel = hiltViewModel()
    val mode by searchVm.modeState.collectAsState()

    BookSearchScreen(
        vm = searchVm,
        onLoadMore = { searchVm.loadMore() },
        onQueryChanged = { searchVm.changeQuery(newValue = it) },
        onModeChanged = { searchVm.changeSearchMode(newMode = it) },
        onSelectBook = { book ->
            searchVm.selectBook(book)
            onGoToConfirm()
        },
        onRefresh = { searchVm.refresh() },
        onManualAdd = { prefill ->
            searchVm.setManualPrefill(prefill, mode)
            onGoToConfirm()
        },
    )
}
