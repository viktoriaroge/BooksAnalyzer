package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.data.remote.NetworkErrorMapper
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BookMapper.toBookOrNull
import com.viroge.booksanalyzer.domain.BooksUtil.normalizeIsbn
import com.viroge.booksanalyzer.domain.SearchMode

class OpenLibraryClient(
    private val api: OpenLibraryApi,
) {

    suspend fun search(
        searchMode: SearchMode,
        query: String,
        limit: Int = 10,
        page: Int = 1,
    ): Result<List<Book>> = runCatching {

        val resp = api.search(
            query = normalizeQuery(mode = searchMode, rawQuery = query),
            page = page,
            limit = limit,
        )
        resp.docs.mapNotNull { it.toBookOrNull() }
    }.mapError()

    fun normalizeQuery(mode: SearchMode, rawQuery: String): String {

        val q = rawQuery.trim()
        if (q.isBlank()) return ""

        // MVP: OL can stay mostly plain text. For ISBN, we help it.
        return when (mode) {
            SearchMode.ISBN -> "isbn:${normalizeIsbn(input = q)}"
            else -> q
        }
    }

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(value = it) },
        onFailure = { Result.failure(exception = NetworkErrorMapper.map(it)) })
}
