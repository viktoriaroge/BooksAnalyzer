package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.domain.BookCandidate
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun observeLibrary(): Flow<List<BookEntity>>

    fun observeBook(
        bookId: String,
    ): Flow<BookEntity?>

    suspend fun insertFromCandidate(
        candidate: BookCandidate,
    ): String

    suspend fun search(
        query: String,
    ): SearchResult

    suspend fun lookupByIsbn(
        isbn: String,
    ): SearchResult

    sealed class SearchResult {

        data class Success(
            val items: List<BookCandidate>,
        ) : SearchResult()

        data class Partial(
            val items: List<BookCandidate>,
            val errors: List<Throwable>,
        ) : SearchResult()

        data class Failure(
            val errors: List<Throwable>,
        ) : SearchResult()
    }
}
