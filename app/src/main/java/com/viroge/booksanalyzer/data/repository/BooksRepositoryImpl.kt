package com.viroge.booksanalyzer.data.repository

import com.viroge.booksanalyzer.data.common.util.PageTokenUtil.makePageToken
import com.viroge.booksanalyzer.data.common.util.PageTokenUtil.parsePageToken
import com.viroge.booksanalyzer.data.local.BooksEntityMapper
import com.viroge.booksanalyzer.data.local.books.BookDao
import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksMapper
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryMapper
import com.viroge.booksanalyzer.data.common.util.BooksUtil.mergeAndRank
import com.viroge.booksanalyzer.data.common.util.BooksUtil.titleKey
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BooksPage
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.model.SearchMode
import com.viroge.booksanalyzer.domain.model.TempBook
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
    private val booksEntityMapper: BooksEntityMapper,
    private val googleBooksClient: GoogleBooksClient,
    private val googleBooksMapper: GoogleBooksMapper,
    private val openLibraryClient: OpenLibraryClient,
    private val openLibraryMapper: OpenLibraryMapper,
) : BooksRepository {

    override fun observeLibrary(): Flow<List<Book>> = bookDao.observeAll()
        .map { list -> list.filter { entity -> !entity.toBeDeleted } }
        .map { list ->
            list.map { entity ->
                val headers = getBookCoverHeaders(url = entity.coverUrl)
                booksEntityMapper.map(entity, headers)
            }
        }

    override fun observePendingDeleteBooks(): Flow<List<Book>> = bookDao.observeAll()
        .map { list -> list.filter { entity -> entity.toBeDeleted } }
        .map { list ->
            list.map { entity ->
                val headers = getBookCoverHeaders(url = entity.coverUrl)
                booksEntityMapper.map(entity, headers)
            }
        }

    override fun observeBook(
        bookId: String,
    ): Flow<Book?> = bookDao.observeById(bookId).map { nullableEntity ->
        nullableEntity?.let { entity ->
            val headers = getBookCoverHeaders(url = entity.coverUrl)
            booksEntityMapper.map(entity, headers)
        }
    }

    override suspend fun getBook(
        bookId: String,
    ): Book? = bookDao.getById(bookId)?.let { entity ->
        val headers = getBookCoverHeaders(url = entity.coverUrl)
        booksEntityMapper.map(entity, headers)
    }

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
        return bookDao.getPendingDeleteBooks().orEmpty().map { entity ->
            val headers = getBookCoverHeaders(url = entity.coverUrl)
            booksEntityMapper.map(entity, headers)
        }
    }

    override suspend fun deleteBook(
        bookId: String,
    ) {
        bookDao.deleteById(bookId)
    }


    override suspend fun insertFromBook(
        book: TempBook,
    ): InsertBookResult {

        book.isbn13?.let { isbn13OfBook ->
            bookDao.findByIsbn13(isbn13 = isbn13OfBook)?.let { existing ->
                bookDao.upsert(
                    book = existing.copy(
                        toBeDeleted = false,
                        coverUrl = book.coverUrl,
                    )
                )
                val headers = getBookCoverHeaders(url = existing.coverUrl)
                return InsertBookResult(
                    booksEntityMapper.map(existing, headers),
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
                val headers = getBookCoverHeaders(url = existing.coverUrl)
                return InsertBookResult(
                    booksEntityMapper.map(existing, headers),
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
                val headers = getBookCoverHeaders(url = existing.coverUrl)
                return InsertBookResult(
                    booksEntityMapper.map(existing, headers),
                    wasInserted = false,
                )
            }
        }

        val key = titleKey(book.title, book.authors, book.year)
        bookDao.findByTitleKey(titleKey = key)?.let { existing ->
            bookDao.upsert(
                book = existing.copy(
                    toBeDeleted = false,
                    coverUrl = book.coverUrl,
                )
            )
            val headers = getBookCoverHeaders(url = existing.coverUrl)
            return InsertBookResult(
                booksEntityMapper.map(existing, headers),
                wasInserted = false,
            )
        }

        // Not in DB, add a new entry:
        val id = UUID.randomUUID().toString()
        val entity = BookEntity(
            bookId = id,
            titleKey = key,
            sourceId = book.sourceId,
            source = book.source.name,
            title = book.title,
            authors = book.authors.joinToString(separator = ", "),
            publishedYear = book.year?.trim()?.takeIf { it.isNotBlank() },
            isbn13 = book.isbn13?.trim()?.takeIf { it.isNotBlank() },
            isbn10 = book.isbn10?.trim()?.takeIf { it.isNotBlank() },
            coverUrl = book.coverUrl?.trim()?.takeIf { it.isNotBlank() },
            status = ReadingStatus.NOT_STARTED.name,
            createdAtEpochMs = System.currentTimeMillis(),
            lastOpenAtEpochMs = System.currentTimeMillis(),
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )
        bookDao.upsert(book = entity)

        val headers = getBookCoverHeaders(url = entity.coverUrl)
        return InsertBookResult(
            book = booksEntityMapper.map(entity, headers),
            wasInserted = true,
        )
    }

    override suspend fun editBook(
        bookId: String,
        title: String,
        authors: String,
        year: String?,
        isbn13: String?,
        isbn10: String?,
        coverUrl: String?,
    ) {
        val existing = bookDao.getById(bookId) ?: return

        bookDao.upsert(
            book = existing.copy(
                titleKey = titleKey(title, authors, year),
                title = title,
                authors = authors,
                publishedYear = year,
                isbn13 = isbn13,
                isbn10 = isbn10,
                coverUrl = coverUrl,
            )
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
            googleBooksClient.search(
                searchMode = searchMode,
                query = query,
                startIndex = token.googleStart,
            ).map { items ->
                items.map { item -> googleBooksMapper.map(item) }
            }
        }
        val o = async {
            openLibraryClient.search(
                searchMode = searchMode,
                query = query,
                page = token.olPage,
            ).map { items ->
                items.mapNotNull { item -> openLibraryMapper.mapOrNull(item) }
            }
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

    override fun getBookCoverHeaders(url: String?): Map<String, String> = when {
        url == null -> emptyMap()

        // Open Library
        openLibraryMapper.isUrlValid(url) -> openLibraryMapper.getHeaders()

        // Google Books
        googleBooksMapper.isUrlValid(url) -> googleBooksMapper.getHeaders()

        else -> emptyMap()
    }
}
