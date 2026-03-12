package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalLibrary
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.nav.Routes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsMapper @Inject constructor() {

    fun map(
        types: List<SettingsItemType>,
        version: String,
    ): SettingsUiState = SettingsUiState(
        screenTitleRes = R.string.settings_screen_name,
        rowStates = types.map { type ->
            when (type) {
                SettingsItemType.BOOKS_HEADER -> SettingsRowState(
                    icon = Icons.Default.LocalLibrary,
                    isHeader = true,
                    showTitle = true,
                    titleRes = R.string.settings_screen_books_section_title,
                )

                SettingsItemType.RECENTLY_DELETED -> SettingsRowState(
                    showTitle = true,
                    titleRes = R.string.recently_deleted_screen_name,
                    showSubtitle = true,
                    subtitleRes = R.string.settings_screen_item_recently_deleted_subtitle,
                    route = Routes.RECENTLY_DELETED_BOOKS,
                )

                SettingsItemType.GUIDE_HEADER -> SettingsRowState(
                    icon = Icons.Default.AutoStories,
                    isHeader = true,
                    showTitle = true,
                    titleRes = R.string.settings_screen_guide_section_title,
                )

                SettingsItemType.TERMS -> SettingsRowState(
                    showTitle = true,
                    titleRes = R.string.settings_screen_item_terms_title,
                    showSubtitle = true,
                    subtitleRes = R.string.settings_screen_item_terms_subtitle,
                    route = Routes.APP_TERMS,
                )

                SettingsItemType.APP_HEADER -> SettingsRowState(
                    icon = Icons.Default.Adb,
                    isHeader = true,
                    showTitle = true,
                    titleRes = R.string.settings_screen_application_section_title,
                )

                SettingsItemType.VERSION -> SettingsRowState(
                    isEnabled = false,
                    showTitle = true,
                    titleRes = R.string.settings_screen_item_version_title,
                    showSubtitle = true,
                    subtitle = version,
                )
            }
        })
}
