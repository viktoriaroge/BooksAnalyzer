package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.books.BookDao
import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BookSource
import com.viroge.booksanalyzer.domain.BooksPage
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.BooksUtil.titleKey
import com.viroge.booksanalyzer.domain.PageToken
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.domain.SearchMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val googleClient: GoogleBooksClient,
    private val openLibraryClient: OpenLibraryClient,
) : BooksRepository {

    override fun observeLibrary(): Flow<List<BookEntity>> = bookDao.observeAll()

    override fun observeBook(
        bookId: String,
    ): Flow<BookEntity?> = bookDao.observeById(bookId)

    override suspend fun updateStatus(
        bookId: String,
        status: ReadingStatus,
    ) {
        val existing = bookDao.getById(bookId) ?: return

        bookDao.upsert(book = existing.copy(status = status.name))
    }

    override suspend fun updateOnOpen(
        bookId: String,
    ) {
        val existing = bookDao.getById(bookId) ?: return

        bookDao.upsert(book = existing.copy(lastOpenAtEpochMs = System.currentTimeMillis()))
    }

    override suspend fun deleteBook(
        bookId: String,
    ) {
        bookDao.deleteById(bookId)
    }

    override suspend fun upsert(
        book: BookEntity,
    ) {
        bookDao.upsert(book)
    }

    override suspend fun deleteAndReturn(
        bookId: String,
    ): BookEntity? {
        val existing = bookDao.getById(bookId) ?: return null

        bookDao.deleteById(bookId)
        return existing
    }

    override suspend fun insertFromBook(
        book: Book,
    ): InsertBookResult {

        book.isbn13?.let { isbn13OfBook ->
            bookDao.findByIsbn13(isbn13 = isbn13OfBook)?.let {
                return InsertBookResult(
                    it.bookId,
                    wasInserted = false,
                )
            }
        }
        book.isbn10?.let { isbn10OfBook ->
            bookDao.findByIsbn10(isbn10 = isbn10OfBook)?.let {
                return InsertBookResult(
                    it.bookId,
                    wasInserted = false,
                )
            }
        }
        book.sourceId?.let { sourceId ->
            bookDao.findBySourceId(sourceId = sourceId)
                ?.let {
                    return InsertBookResult(
                        it.bookId,
                        wasInserted = false,
                    )
                }
        }

        val key = titleKey(book.title, book.authors, book.publishedYear)
        bookDao.findByTitleKey(titleKey = key)?.let {
            return InsertBookResult(
                it.bookId,
                wasInserted = false,
            )
        }

        val id = UUID.randomUUID().toString()
        val entity = BookEntity(
            bookId = id,
            sourceId = book.sourceId,
            source = book.source.name,
            title = book.title,
            authors = book.authors.joinToString(separator = ", "),
            titleKey = key,
            publishedYear = book.publishedYear,
            isbn13 = book.isbn13,
            isbn10 = book.isbn10,
            coverUrl = book.coverUrl,
            status = book.status.name,
            createdAtEpochMs = System.currentTimeMillis(),
            lastOpenAtEpochMs = System.currentTimeMillis(),
        )
        bookDao.upsert(book = entity)

        return InsertBookResult(
            bookId = id,
            wasInserted = true,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun searchPage(
        searchMode: SearchMode,
        query: String,
        pageToken: String?,
        limit: Int,
    ): BooksPage = coroutineScope {

        val token = parseToken(pageToken)

        val g = async {
            googleClient.search(
                searchMode = searchMode,
                query = query,
                limit = limit,
                startIndex = token.googleStart,
            )
        }
        val o = async {
            openLibraryClient.search(
                searchMode = searchMode,
                query = query,
                limit = limit,
                page = token.olPage,
            )
        }

        val results = listOf(g.await(), o.await())
        val items = results.flatMap { it.getOrNull().orEmpty() }
        val errors = results.mapNotNull { it.exceptionOrNull() }

        val merged = mergeAndRank(list = items)

        // crude but effective "has more" heuristic:
        val googleReturned = g.getCompleted().getOrNull().orEmpty().size
        val olReturned = o.getCompleted().getOrNull().orEmpty().size

        val googleHasMore = googleReturned >= limit
        val olHasMore = olReturned >= limit

        val next = if (googleHasMore || olHasMore) {
            makeToken(
                nextGoogleStart = token.googleStart + limit,
                nextOlPage = token.olPage + 1
            )
        } else null

        BooksPage(
            items = merged,
            errors = errors,
            nextToken = next,
        )
    }

    private fun parseToken(
        token: String?,
    ): PageToken {

        if (token.isNullOrBlank()) return PageToken(googleStart = 0, olPage = 1)

        val parts = token.split("|")
        val g = parts
            .firstOrNull { it.startsWith("g:") }
            ?.removePrefix("g:")
            ?.toIntOrNull() ?: 0
        val ol = parts
            .firstOrNull { it.startsWith("ol:") }
            ?.removePrefix("ol:")
            ?.toIntOrNull() ?: 1
        return PageToken(g, ol)
    }

    private fun makeToken(
        nextGoogleStart: Int,
        nextOlPage: Int,
    ): String = "g:$nextGoogleStart|ol:$nextOlPage"
}
