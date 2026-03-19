package com.viroge.booksanalyzer.domain.usecase.search

import com.viroge.booksanalyzer.data.remote.ApiSource
import com.viroge.booksanalyzer.data.remote.AppNetworkError
import com.viroge.booksanalyzer.data.repository.BooksRepository
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
            when (error) {
                is AppNetworkError.NoConnection -> SearchError.NoConnection(ApiSource.N_A)
                is AppNetworkError.Timeout -> SearchError.Timeout(error.source)
                is AppNetworkError.Security -> SearchError.SecurityError(error.source)
                is AppNetworkError.Cancelled -> SearchError.Cancelled(error.source)
                is AppNetworkError.Http -> {
                    if (error.code == 429) SearchError.RateLimit(error.source)
                    else SearchError.Unknown(error.source)
                }

                is AppNetworkError.Unknown -> SearchError.Unknown(error.source)
                else -> SearchError.Unknown(ApiSource.N_A)
            }
        }.distinct().firstOrNull() ?: SearchError.None

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

sealed class SearchError(val apiSource: ApiSource = ApiSource.N_A) {
    data class NoConnection(val source: ApiSource) : SearchError(source)
    data class Timeout(val source: ApiSource) : SearchError(source)
    data class Cancelled(val source: ApiSource) : SearchError(source)
    data class SecurityError(val source: ApiSource) : SearchError(source)
    data class RateLimit(val source: ApiSource) : SearchError(source)
    data class Unknown(val source: ApiSource) : SearchError(source)
    object None : SearchError()
}
