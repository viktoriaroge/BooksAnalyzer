package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val booksRepo: BooksRepository
) {
    suspend operator fun invoke(
        query: String,
        mode: SearchMode,
        pageToken: String? = null,
    ): SearchResult {
        val page = booksRepo.searchPage(
            searchMode = mode,
            query = query,
            pageToken = pageToken,
        )

        val error = page.errors.map { error ->
            val msg = error.message ?: error.javaClass.simpleName

            if (msg.lowercase().contains("noconnection")) SearchError.NO_CONNECTION
            else SearchError.UNKNOWN
        }.distinct().firstOrNull() ?: SearchError.NONE

        return SearchResult(
            items = page.items,
            nextToken = page.nextToken,
            error = error,
        )
    }
}

data class SearchResult(
    val items: List<TempBook>,
    val nextToken: String?,
    val error: SearchError,
)

enum class SearchError {
    NO_CONNECTION,
    UNKNOWN,
    NONE,
}
