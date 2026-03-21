package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalLibrary
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
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
                    id = type.name,
                    icon = Icons.Default.LocalLibrary,
                    isHeader = true,
                    title = UiText.StringResource(R.string.settings_screen_books_section_title),
                )

                SettingsItemType.RECENTLY_DELETED -> SettingsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.recently_deleted_screen_name),
                    subtitle = UiText.StringResource(R.string.settings_screen_item_recently_deleted_subtitle),
                    route = Routes.RECENTLY_DELETED_BOOKS,
                )

                SettingsItemType.GUIDE_HEADER -> SettingsRowState(
                    id = type.name,
                    icon = Icons.Default.AutoStories,
                    isHeader = true,
                    title = UiText.StringResource(R.string.settings_screen_guide_section_title),
                )

                SettingsItemType.TERMS -> SettingsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.settings_screen_item_terms_title),
                    subtitle = UiText.StringResource(R.string.settings_screen_item_terms_subtitle),
                    route = Routes.APP_TERMS,
                )

                SettingsItemType.APP_HEADER -> SettingsRowState(
                    id = type.name,
                    icon = Icons.Default.Adb,
                    isHeader = true,
                    title = UiText.StringResource(R.string.settings_screen_application_section_title),
                )

                SettingsItemType.VERSION -> SettingsRowState(
                    id = type.name,
                    isEnabled = false,
                    title = UiText.StringResource(R.string.settings_screen_item_version_title),
                    subtitle = UiText.DynamicString(version),
                )
            }
        })
}
