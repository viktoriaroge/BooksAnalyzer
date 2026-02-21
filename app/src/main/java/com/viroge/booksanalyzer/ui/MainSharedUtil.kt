package com.viroge.booksanalyzer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun activityViewModel(): MainSharedViewModel {
    val activity = LocalContext.current as MainActivity
    return hiltViewModel(viewModelStoreOwner = activity)
}
