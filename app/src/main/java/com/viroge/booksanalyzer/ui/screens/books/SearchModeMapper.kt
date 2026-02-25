package com.viroge.booksanalyzer.ui.screens.books

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.SearchMode

object SearchModeMapper {

    @Composable
    fun getUiModel(mode: SearchMode): SearchModeUiModel {
        val text = when (mode) {
            SearchMode.ALL -> stringResource(R.string.search_mode_all)
            SearchMode.TITLE -> stringResource(R.string.search_mode_title)
            SearchMode.AUTHOR -> stringResource(R.string.search_mode_author)
            SearchMode.ISBN -> stringResource(R.string.search_mode_isbn)
        }
        return SearchModeUiModel(mode, text)
    }
}

data class SearchModeUiModel(
    val mode: SearchMode,
    val text: String,
)
