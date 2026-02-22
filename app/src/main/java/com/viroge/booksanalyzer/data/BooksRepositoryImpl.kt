package com.viroge.booksanalyzer.data

import com.viroge.booksanalyzer.data.local.books.BookDao
import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import com.viroge.booksanalyzer.domain.Book
import com.viroge.booksanalyzer.domain.BookMapper.toBook
import com.viroge.booksanalyzer.domain.BooksPage
import com.viroge.booksanalyzer.domain.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.domain.BooksUtil.titleKey
import com.viroge.booksanalyzer.domain.PageTokenHandler.makePageToken
import com.viroge.booksanalyzer.domain.PageTokenHandler.parsePageToken
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.domain.SearchMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val googleClient: GoogleBooksClient,
    private val openLibraryClient: OpenLibraryClient,
) : BooksRepository {

    override fun observeLibrary(): Flow<List<Book>> = bookDao.observeAll()
        .map { list -> list.filter { entity -> !entity.toBeDeleted } }
        .map { list -> list.map { entity -> entity.toBook() } }

    override fun observeBook(
        bookId: String,
    ): Flow<Book?> = bookDao.observeById(bookId).map { it?.toBook() }

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

    override suspend fun markBookToDelete(
        bookId: String,
    ): Pair<String, String>? {
        val existing = bookDao.getById(bookId) ?: return null

        bookDao.upsert(book = existing.copy(toBeDeleted = true, lastMarkedToDelete = System.currentTimeMillis()))
        return Pair(first = existing.bookId, second = existing.title)
    }

    override suspend fun restoreBookMarkedToDelete(
        bookId: String,
    ) {
        val existing = bookDao.getById(bookId) ?: return

        bookDao.upsert(book = existing.copy(toBeDeleted = false))
    }

    override suspend fun getPendingDeleteBooks(): List<Book> {
        return bookDao.getPendingDeleteBooks().orEmpty().map { it.toBook() }
    }

    override suspend fun deleteBook(
        bookId: String,
    ) {
        bookDao.deleteById(bookId)
    }

    override suspend fun insertFromBook(
        book: Book,
        wasEdited: Boolean,
    ): InsertBookResult {

        if (!wasEdited) {
            book.isbn13?.let { isbn13OfBook ->
                bookDao.findByIsbn13(isbn13 = isbn13OfBook)?.let { existing ->
                    bookDao.upsert(
                        book = existing.copy(
                            toBeDeleted = false,
                            coverUrl = book.coverUrl,
                        )
                    )
                    return InsertBookResult(
                        existing.bookId,
                        wasInserted = false,
                    )
                }
            }
            book.isbn10?.let { isbn10OfBook ->
                bookDao.findByIsbn10(isbn10 = isbn10OfBook)?.let { existing ->
                    bookDao.upsert(
                        book = existing.copy(
                            toBeDeleted = false,
                            coverUrl = book.coverUrl,
                        )
                    )
                    return InsertBookResult(
                        existing.bookId,
                        wasInserted = false,
                    )
                }
            }
            book.sourceId?.let { sourceId ->
                bookDao.findBySourceId(sourceId = sourceId)?.let { existing ->
                    bookDao.upsert(
                        book = existing.copy(
                            toBeDeleted = false,
                            coverUrl = book.coverUrl,
                        )
                    )
                    return InsertBookResult(
                        existing.bookId,
                        wasInserted = false,
                    )
                }
            }

            val key = titleKey(book.title, book.authors, book.publishedYear)
            bookDao.findByTitleKey(titleKey = key)?.let { existing ->
                bookDao.upsert(
                    book = existing.copy(
                        toBeDeleted = false,
                        coverUrl = book.coverUrl,
                    )
                )
                return InsertBookResult(
                    existing.bookId,
                    wasInserted = false,
                )
            }
        }

        val id = if (wasEdited) book.id else UUID.randomUUID().toString()
        val key = titleKey(book.title, book.authors, book.publishedYear)
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
            lastMarkedToDelete = 0,
            toBeDeleted = false,
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
    ): BooksPage = coroutineScope {

        val token = parsePageToken(pageToken)

        val g = async {
            googleClient.search(
                searchMode = searchMode,
                query = query,
                startIndex = token.googleStart,
            )
        }
        val o = async {
            openLibraryClient.search(
                searchMode = searchMode,
                query = query,
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

        val googleHasMore = googleReturned >= GoogleBooksClient.ITEMS_PER_PAGE
        val olHasMore = olReturned >= OpenLibraryClient.ITEMS_PER_PAGE

        val next = if (googleHasMore || olHasMore) {
            makePageToken(
                nextGoogleStart = token.googleStart + GoogleBooksClient.ITEMS_PER_PAGE,
                nextOlPage = token.olPage + 1,
            )
        } else null

        BooksPage(
            items = merged,
            errors = errors,
            nextToken = next,
        )
    }
}
