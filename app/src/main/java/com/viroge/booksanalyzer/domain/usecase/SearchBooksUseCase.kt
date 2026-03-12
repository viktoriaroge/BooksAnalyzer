package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.SearchMode
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

        val messages = page.errors.map { error ->
            val msg = error.message ?: error.javaClass.simpleName
            if (msg.lowercase().contains("noconnection")) {
                "Check your Internet connection."
            } else msg
        }.distinct()

        return SearchResult(
            items = page.items,
            messages = messages,
            nextToken = page.nextToken
        )
    }
}

data class SearchResult(
    val items: List<Book>,
    val messages: List<String>,
    val nextToken: String?,
)
