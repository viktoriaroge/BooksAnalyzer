package com.viroge.booksanalyzer.ui.screens.terms

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText

@Immutable
data class TermsUiState(
    @param:StringRes val screenTitleRes: Int = R.string.empty_text,
    @param:DrawableRes val introIconRes: Int = R.drawable.ic_launcher_foreground,
    @param:StringRes val introTextRes: Int = R.string.empty_text,
    val rowStates: List<TermsRowState> = emptyList(),
)

@Immutable
data class TermsRowState(
    val id: String,
    val title: UiText? = null,
    val subtitle: UiText? = null,
)
