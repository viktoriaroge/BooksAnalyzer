package com.viroge.booksanalyzer.ui.screens.books.recentlydeleted

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar

@Composable
fun RecentlyDeletedRoute(onBack: () -> Unit) {

    val vm: RecentlyDeletedViewModel = hiltViewModel()
    val uiState by vm.state.collectAsState()

    val snackbar = LocalAppSnackbar.current
    LaunchedEffect(key1 = Unit) {
        vm.message.collect { newMessage ->
            newMessage?.let { snackbar.show(message = it, duration = SnackbarDuration.Short) }
        }
    }

    RecentlyDeletedScreen(
        state = uiState,
        onRestoreBook = vm::restoreBook,
        onBack = onBack,
    )
}
