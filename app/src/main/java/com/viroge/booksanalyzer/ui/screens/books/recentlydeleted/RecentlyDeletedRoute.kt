package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecentlyDeletedRoute(onBack: () -> Unit) {

    val vm: RecentlyDeletedViewModel = hiltViewModel()
    val uiState by vm.state.collectAsState()

    RecentlyDeletedScreen(
        state = uiState,
        onRestoreBook = {},
        onBack = onBack,
    )
}
