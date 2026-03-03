package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.data.remote.NetworkErrorMapper
import com.viroge.booksanalyzer.domain.BooksUtil.normalizeIsbn
import com.viroge.booksanalyzer.domain.model.library.SearchMode

class OpenLibraryClient(
    private val api: OpenLibraryApi,
) {

    companion object {
        const val ITEMS_PER_PAGE = 100 // max allowed by API
    }

    suspend fun search(
        searchMode: SearchMode,
        query: String,
        page: Int = 1,
    ): Result<List<OpenLibraryDoc>> = runCatching {

        val resp = api.search(
            query = normalizeQuery(mode = searchMode, rawQuery = query),
            page = page,
            limit = ITEMS_PER_PAGE,
        )
        resp.docs
    }.mapError()

    fun normalizeQuery(mode: SearchMode, rawQuery: String): String {

        val q = rawQuery.trim()
        if (q.isBlank()) return ""

        return when (mode) {
            SearchMode.ALL -> q
            SearchMode.TITLE -> "title:$q"
            SearchMode.AUTHOR -> "author:$q"
            SearchMode.ISBN -> "isbn:${normalizeIsbn(input = q)}"
        }
    }

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(value = it) },
        onFailure = { Result.failure(exception = NetworkErrorMapper.map(it)) })
}
