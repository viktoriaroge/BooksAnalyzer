package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.data.remote.NetworkErrorMapper
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BookMapper.toBook
import com.viroge.booksanalyzer.domain.BooksUtil.normalizeIsbn
import com.viroge.booksanalyzer.domain.SearchMode

class GoogleBooksClient(
    private val api: GoogleBooksApi,
    private val apiKey: String,
) {

    companion object {
        const val ITEMS_PER_PAGE = 40 // max allowed by API
    }

    suspend fun search(
        searchMode: SearchMode,
        query: String,
        startIndex: Int = 0,
    ): Result<List<Book>> = runCatching {

        val resp = api.searchVolumes(
            query = normalizeQuery(mode = searchMode, rawQuery = query),
            startIndex = startIndex,
            maxResults = ITEMS_PER_PAGE,
            apiKey = apiKey,
        )
        resp.items.map { it.toBook() }
    }.mapError()

    private fun normalizeQuery(
        mode: SearchMode,
        rawQuery: String,
    ): String {

        val q = rawQuery.trim()
        if (q.isBlank()) return ""

        return when (mode) {
            SearchMode.ALL -> q
            SearchMode.TITLE -> """intitle:${quoteIfNeeded(input = q)}"""
            SearchMode.AUTHOR -> """inauthor:${quoteIfNeeded(input = q)}"""
            SearchMode.ISBN -> """isbn:${normalizeIsbn(input = q)}"""
        }
    }

    private fun quoteIfNeeded(input: String): String =
        if (input.contains(char = ' ')) "\"$input\"" else input

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(value = it) },
        onFailure = { Result.failure(exception = NetworkErrorMapper.map(it)) })
}
