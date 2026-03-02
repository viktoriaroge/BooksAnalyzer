package com.viroge.booksanalyzer.ui.screens.terms

import com.viroge.booksanalyzer.R
import javax.inject.Inject

class TermsMapper @Inject constructor() {

    fun map(
        types: List<TermsItemType>,
    ): TermsUiState = TermsUiState(
        screenTitleRes = R.string.terms_screen_name,
        introIconRes = R.drawable.ic_launcher_foreground,
        introTextRes = R.string.terms_screen_intro,
        rowStates = types.map { type ->
            when (type) {
                TermsItemType.PAGEVOW -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_pagevow_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_pagevow_desc,
                )

                TermsItemType.BOOK_COVER -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_visage_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_visage_desc,
                )

                TermsItemType.LIBRARY -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_chronicle_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_chronicle_desc,
                )

                TermsItemType.DELETE_BOOK -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_banish_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_banish_desc,
                )

                TermsItemType.RECENTLY_DELETED_BOOKS -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_exile_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_exile_desc,
                )

                TermsItemType.SETTINGS -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_scriptorium_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_scriptorium_desc,
                )

                TermsItemType.SOURCE -> TermsRowState(
                    showTitle = true,
                    titleRes = R.string.terms_screen_origin_title,
                    showSubtitle = true,
                    subtitleRes = R.string.terms_screen_origin_desc,
                )
            }
        })
}
