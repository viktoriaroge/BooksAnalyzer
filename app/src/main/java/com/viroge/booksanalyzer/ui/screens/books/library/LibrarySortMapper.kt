package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.library.LibrarySort

object LibrarySortMapper {

    @Composable
    fun getUiModel(
        sort: LibrarySort,
    ): LibrarySortUiModel {
        val text = when (sort) {
            LibrarySort.ADDED -> stringResource(R.string.library_sort_by_added)
            LibrarySort.RECENT -> stringResource(R.string.library_sort_by_recent)
            LibrarySort.TITLE -> stringResource(R.string.library_sort_by_title)
            LibrarySort.AUTHOR -> stringResource(R.string.library_sort_by_author)
        }
        return LibrarySortUiModel(sort, text)
    }
}

data class LibrarySortUiModel(
    val sort: LibrarySort,
    val text: String,
)
