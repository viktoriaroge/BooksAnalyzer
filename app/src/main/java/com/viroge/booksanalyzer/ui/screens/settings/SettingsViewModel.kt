package com.viroge.booksanalyzer.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.viroge.booksanalyzer.R
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
                    showTitle = true,
                    titleRes = R.string.settings_screen_profile_section_title,
                ),
                SettingsEntry(
                    isEnabled = false,
                    showTitle = true,
                    titleRes = R.string.settings_screen_item_account_title,
                    showSubtitle = true,
                    subtitleRes = R.string.settings_screen_item_account_subtitle,
                ),

                // --- Books --------------------
                SettingsEntry(
                    icon = Icons.Default.LocalLibrary,
                    isHeader = true,
                    showTitle = true,
                    titleRes = R.string.settings_screen_books_section_title,
                ),
                SettingsEntry(
                    showTitle = true,
                    titleRes = R.string.recently_deleted_screen_name,
                    showSubtitle = true,
                    subtitleRes = R.string.settings_screen_item_recently_deleted_subtitle,
                    route = Routes.RECENTLY_DELETED_BOOKS,
                ),

                // --- App ----------------------
                SettingsEntry(
                    icon = Icons.Default.Adb,
                    isHeader = true,
                    showTitle = true,
                    titleRes = R.string.settings_screen_application_section_title,
                ),
                SettingsEntry(
                    isEnabled = false,
                    showTitle = true,
                    titleRes = R.string.settings_screen_item_version_title,
                    showSubtitle = true,
                    subtitleRes = R.string.settings_screen_item_version_subtitle,
                ),
            ),
        )
    }
}

data class SettingsUiState(
    val settingsEntries: List<SettingsEntry> = emptyList(),
)

data class SettingsEntry(
    val isHeader: Boolean = false,
    val isEnabled: Boolean = true,
    val route: String? = null,
    val icon: ImageVector? = null,
    val showTitle: Boolean = false,
    val title: String? = null,
    @param:StringRes val titleRes: Int = R.string.empty_text,
    val showSubtitle: Boolean = false,
    val subtitle: String? = null,
    @param:StringRes val subtitleRes: Int = R.string.empty_text,
)

