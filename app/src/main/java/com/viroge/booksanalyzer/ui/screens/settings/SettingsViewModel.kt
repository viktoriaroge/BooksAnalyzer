package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.viroge.booksanalyzer.ui.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(value = SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        _state.value = SettingsUiState(
            settingsEntries = listOf(
                // --- Profile -------------------
                SettingsEntry(
                    icon = Icons.Default.Person,
                    isHeader = true,
                    title = "Profile",
                ),
                SettingsEntry(
                    isEnabled = false,
                    title = "Account & Sync (coming soon)",
                    subtitle = "Sign in to sync your books across devices.",
                ),

                // --- Books --------------------
                SettingsEntry(
                    icon = Icons.Default.LocalLibrary,
                    isHeader = true,
                    title = "Books",
                ),
                SettingsEntry(
                    title = "Recently Deleted",
                    subtitle = "Books are kept here for 7 days before permanent removal.",
                    route = Routes.RECENTLY_DELETED_BOOKS,
                ),

                // --- App ----------------------
                SettingsEntry(
                    icon = Icons.Default.Adb,
                    isHeader = true,
                    title = "Application",
                ),
                SettingsEntry(
                    isEnabled = false,
                    title = "Version",
                    subtitle = "1.0.0",
                ),
            ),
        )
    }
}

data class SettingsUiState(
    val settingsEntries: List<SettingsEntry> = emptyList(),
)

data class SettingsEntry(
    val icon: ImageVector? = null,
    val title: String,
    val isHeader: Boolean = false,
    val isEnabled: Boolean = true,
    val subtitle: String? = null,
    val route: String? = null,
)

