package com.viroge.booksanalyzer.ui.screens.terms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TermsRoute(
    onBack: () -> Unit,
) {
    val vm: TermsViewModel = hiltViewModel()
    val uiState by vm.state.collectAsStateWithLifecycle()

    TermsScreen(
        state = uiState,
        onBack = onBack,
    )
}
