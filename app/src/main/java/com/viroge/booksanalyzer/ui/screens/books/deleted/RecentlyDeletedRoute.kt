package com.viroge.booksanalyzer.ui.screens.books.deleted

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar

@Composable
fun RecentlyDeletedRoute(
    onBack: () -> Unit,
) {
    val vm: RecentlyDeletedViewModel = hiltViewModel()
    val uiState by vm.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbar = LocalAppSnackbar.current

    LaunchedEffect(vm.messages, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.messages.collect { newMessage ->
                newMessage?.let {
                    snackbar.show(message = it, duration = SnackbarDuration.Short)
                }
            }
        }
    }

    RecentlyDeletedScreen(
        state = uiState,
        onRestoreBook = vm::restoreBook,
        onBack = onBack,
    )
}
