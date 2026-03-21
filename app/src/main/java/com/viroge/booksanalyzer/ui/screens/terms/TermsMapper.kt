package com.viroge.booksanalyzer.ui.screens.terms

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.UiText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_pagevow_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_pagevow_desc),
                )

                TermsItemType.BOOK_COVER -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_visage_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_visage_desc),
                )

                TermsItemType.LIBRARY -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_chronicle_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_chronicle_desc),
                )

                TermsItemType.DELETE_BOOK -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_banish_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_banish_desc),
                )

                TermsItemType.RECENTLY_DELETED_BOOKS -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_exile_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_exile_desc),
                )

                TermsItemType.SETTINGS -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_scriptorium_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_scriptorium_desc),
                )

                TermsItemType.SOURCE -> TermsRowState(
                    id = type.name,
                    title = UiText.StringResource(R.string.terms_screen_origin_title),
                    subtitle = UiText.StringResource(R.string.terms_screen_origin_desc),
                )
            }
        })
}
