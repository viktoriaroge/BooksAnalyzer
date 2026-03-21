package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onOpenEntry: (route: String) -> Unit,
) {
    val vm: SettingsViewModel = hiltViewModel()
    val uiState by vm.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = uiState,
        onOpenEntry = onOpenEntry,
    )
}
