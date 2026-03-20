package com.viroge.booksanalyzer.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun activityViewModel(): MainSharedViewModel {
    val activity = LocalActivity.current as MainActivity
    return hiltViewModel(viewModelStoreOwner = activity)
}
