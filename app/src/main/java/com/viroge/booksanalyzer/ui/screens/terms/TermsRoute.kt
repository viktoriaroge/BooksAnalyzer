package com.viroge.booksanalyzer.ui.screens.terms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TermsRoute(
    onBack: () -> Unit,
) {

    val vm: TermsViewModel = hiltViewModel()
    val uiState by vm.state.collectAsState()

    TermsScreen(
        state = uiState,
        onBack = onBack,
    )
}
