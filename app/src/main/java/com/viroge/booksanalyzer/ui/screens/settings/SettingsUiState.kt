package com.viroge.booksanalyzer.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText

@Immutable
data class SettingsUiState(
    @param:StringRes val screenTitleRes: Int = R.string.empty_text,
    val rowStates: List<SettingsRowState> = emptyList(),
)


@Immutable
data class SettingsRowState(
    val id: String,
    val isHeader: Boolean = false,
    val isEnabled: Boolean = true,
    val route: String? = null,
    val icon: ImageVector? = null,
    val title: UiText? = null,
    val subtitle: UiText? = null,
)
