package com.viroge.booksanalyzer.ui.screens.books

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.SearchMode

object SearchModeMapper {

    @Composable
    fun getUiModel(mode: SearchMode): SearchModeUiModel {
        val (text, color) = when (mode) {
            SearchMode.ALL -> stringResource(R.string.search_mode_all) to MaterialTheme.colorScheme.surfaceContainerHighest
            SearchMode.TITLE -> stringResource(R.string.search_mode_title) to MaterialTheme.colorScheme.surfaceContainerHighest
            SearchMode.AUTHOR -> stringResource(R.string.search_mode_author) to MaterialTheme.colorScheme.surfaceContainerHighest
            SearchMode.ISBN -> stringResource(R.string.search_mode_isbn) to MaterialTheme.colorScheme.surfaceContainerHighest
        }
        return SearchModeUiModel(mode, text, color)
    }
}

data class SearchModeUiModel(
    val mode: SearchMode,
    val text: String,
    val color: Color,
)
