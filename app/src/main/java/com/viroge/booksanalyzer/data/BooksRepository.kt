package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BooksPage
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.domain.SearchMode
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun observeLibrary(): Flow<List<Book>>

    fun observeBook(
        bookId: String,
    ): Flow<Book?>

    suspend fun updateStatus(
        bookId: String,
        status: ReadingStatus,
    )

    suspend fun updateOnOpen(
        bookId: String,
    )

    /**
     * returns a Pair of: first = book id and second = book title if it was inserted, null otherwise
     */
    suspend fun markBookToDelete(
        bookId: String,
    ): Pair<String, String>?

    suspend fun restoreBookMarkedToDelete(
        bookId: String,
    )

    suspend fun getPendingDeleteBooks(): List<Book>

    suspend fun deleteBook(
        bookId: String,
    )

    suspend fun insertFromBook(
        book: Book,
        wasEdited: Boolean = false,
    ): InsertBookResult

    suspend fun searchPage(
        searchMode: SearchMode,
        query: String,
        pageToken: String?, // null = first page
    ): BooksPage
}
