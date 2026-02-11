package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.data.local.InsertBookResult
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.BooksPageResult
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.domain.SearchMode
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun observeLibrary(): Flow<List<BookEntity>>

    fun observeBook(
        bookId: String,
    ): Flow<BookEntity?>

    suspend fun updateStatus(
        bookId: String,
        status: ReadingStatus,
    )

    suspend fun deleteBook(
        bookId: String,
    )

    suspend fun upsert(
        book: BookEntity,
    )

    suspend fun deleteAndReturn(
        bookId: String,
    ): BookEntity?

    suspend fun insertFromCandidate(
        candidate: BookCandidate,
    ): InsertBookResult

    suspend fun searchPage(
        searchMode: SearchMode,
        query: String,
        pageToken: String?, // null = first page
        limit: Int,
    ): BooksPageResult
}
