package com.viroge.booksanalyzer.ui.screens.terms

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.viroge.booksanalyzer.R


data class TermsUiState(
    @param:StringRes val screenTitleRes: Int = R.string.empty_text,
    @param:DrawableRes val introIconRes: Int = R.drawable.ic_launcher_foreground,
    @param:StringRes val introTextRes: Int = R.string.empty_text,
    val rowStates: List<TermsRowState> = emptyList(),
)

data class TermsRowState(
    val showTitle: Boolean = false,
    val title: String? = null,
    @param:StringRes val titleRes: Int = R.string.empty_text,
    val showSubtitle: Boolean = false,
    val subtitle: String? = null,
    @param:StringRes val subtitleRes: Int = R.string.empty_text,
)