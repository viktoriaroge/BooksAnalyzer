package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BooksPage
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.domain.model.TempBook
import kotlinx.coroutines.flow.Flow

interface BooksRepository {

    fun observeLibrary(): Flow<List<Book>>
    fun observePendingDeleteBooks(): Flow<List<Book>>

    fun observeBook(
        bookId: String,
    ): Flow<Book?>

    suspend fun getBook(
        bookId: String,
    ): Book?

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
        book: TempBook,
    ): InsertBookResult

    suspend fun editBook(
        bookId: String,
        title: String,
        authors: String,
        year: String?,
        isbn13: String?,
        isbn10: String?,
        coverUrl: String?,
    )

    suspend fun searchPage(
        searchMode: SearchMode,
        query: String,
        pageToken: String?, // null = first page
    ): BooksPage

    fun getBookCoverHeaders(url: String?): Map<String, String>
}
