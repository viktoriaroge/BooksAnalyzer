package com.viroge.booksanalyzer.ui.screens.settings

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.viroge.booksanalyzer.R

@Immutable
data class SettingsUiState(
    @param:StringRes val screenTitleRes: Int = R.string.empty_text,
    val rowStates: List<SettingsRowState> = emptyList(),
)


@Immutable
data class SettingsRowState(
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
