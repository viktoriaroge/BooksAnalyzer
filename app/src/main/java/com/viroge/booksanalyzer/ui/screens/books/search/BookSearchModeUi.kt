package com.viroge.booksanalyzer.ui.screens.books.search

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.ui.common.util.UiText

sealed class BookSearchModeUi(
    val domainStatus: SearchMode,
    val label: UiText,
) {
    object All : BookSearchModeUi(
        domainStatus = SearchMode.ALL,
        label = UiText.StringResource(R.string.search_mode_all),
    )

    object Title : BookSearchModeUi(
        domainStatus = SearchMode.TITLE,
        label = UiText.StringResource(R.string.search_mode_title),
    )

    object Author : BookSearchModeUi(
        domainStatus = SearchMode.AUTHOR,
        label = UiText.StringResource(R.string.search_mode_author),
    )

    object Isbn : BookSearchModeUi(
        domainStatus = SearchMode.ISBN,
        label = UiText.StringResource(R.string.search_mode_isbn),
    )

    companion object {
        fun allOptions(): List<BookSearchModeUi> = listOf(
            All,
            Title,
            Author,
            Isbn,
        )

        fun fromDomain(mode: SearchMode) = when (mode) {
            SearchMode.ALL -> All
            SearchMode.TITLE -> Title
            SearchMode.AUTHOR -> Author
            SearchMode.ISBN -> Isbn
        }
    }
}
