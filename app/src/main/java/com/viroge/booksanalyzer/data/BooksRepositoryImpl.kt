package com.viroge.booksanalyzer.data

import androidx.collection.LruCache
import com.viroge.booksanalyzer.data.local.BookDao
import com.viroge.booksanalyzer.data.local.BookEntity
import com.viroge.booksanalyzer.data.local.InsertBookResult
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import com.viroge.booksanalyzer.domain.BookCandidate
import com.viroge.booksanalyzer.domain.ReadingStatus
import com.viroge.booksanalyzer.util.BookKeys
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val google: GoogleBooksClient,
    private val openLibrary: OpenLibraryClient,
) : BooksRepository {

    private val resultsCache = LruCache<String, List<BookCandidate>>(/*maxSize*/ 100)

    override fun observeLibrary(): Flow<List<BookEntity>> = bookDao.observeAll()

    override fun observeBook(
        bookId: String,
    ): Flow<BookEntity?> = bookDao.observeById(bookId)

    override suspend fun updateStatus(
        bookId: String,
        status: ReadingStatus,
    ) {
        val existing = bookDao.getById(bookId) ?: return

        bookDao.upsert(existing.copy(status = status.name))
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

    override suspend fun insertFromCandidate(
        candidate: BookCandidate,
    ): InsertBookResult {
        candidate.isbn13?.let {
            bookDao.findByIsbn13(it)?.let { return InsertBookResult(it.bookId, false) }
        }
        candidate.isbn10?.let {
            bookDao.findByIsbn10(it)?.let { return InsertBookResult(it.bookId, false) }
        }

        when (candidate.source) {
            BookCandidate.Source.GOOGLE_BOOKS ->
                bookDao.findByGoogleId(candidate.sourceId)
                    ?.let { return InsertBookResult(it.bookId, false) }

            BookCandidate.Source.OPEN_LIBRARY ->
                bookDao.findByOpenLibraryId(candidate.sourceId)
                    ?.let { return InsertBookResult(it.bookId, false) }
        }

        val key = BookKeys.titleKey(candidate.title, candidate.authors, candidate.publishedYear)
        bookDao.findByTitleKey(key)?.let { return InsertBookResult(it.bookId, false) }

        val id = UUID.randomUUID().toString()
        val entity = BookEntity(
            bookId = id,
            title = candidate.title,
            authors = candidate.authors.joinToString(", "),
            titleKey = key,
            publishedYear = candidate.publishedYear,
            isbn13 = candidate.isbn13,
            isbn10 = candidate.isbn10,
            openLibraryId = candidate.sourceId.takeIf { candidate.source == BookCandidate.Source.OPEN_LIBRARY },
            googleVolumeId = candidate.sourceId.takeIf { candidate.source == BookCandidate.Source.GOOGLE_BOOKS },
            coverUrl = candidate.coverUrl,
            status = "NOT_STARTED",
            createdAtEpochMs = System.currentTimeMillis()
        )
        bookDao.upsert(entity)
        return InsertBookResult(id, true)
    }

    override suspend fun search(
        query: String,
    ): BooksRepository.SearchResult = multiSourceSearch(
        cacheKey = cacheKey(mode = "q", query = query, limit = 15),
        query = query,
        limit = 15,
    )

    override suspend fun lookupByIsbn(
        isbn: String,
    ): BooksRepository.SearchResult = multiSourceSearch(
        cacheKey = cacheKey(mode = "isbn", query = isbn, limit = 10),
        query = "isbn:$isbn",
        limit = 10,
    )

    private suspend fun multiSourceSearch(
        cacheKey: String,
        query: String,
        limit: Int,
    ): BooksRepository.SearchResult {

        // 1) Serve from cache
        resultsCache[cacheKey]?.let { cached ->
            return BooksRepository.SearchResult.Success(cached)
        }

        // 2) Fetch both sources in parallel
        return coroutineScope {
            val g = async { google.search(query, limit) }
            val o = async { openLibrary.search(query, limit) }
            val results = listOf(g.await(), o.await())

            val items = results.flatMap { it.getOrNull().orEmpty() }
            val errors = results.mapNotNull { it.exceptionOrNull() }

            val merged = items.mergeAndRank()

            // 3) Cache only good-enough lists
            if (merged.isNotEmpty()) {
                resultsCache.put(cacheKey, merged)
            }

            when {
                merged.isNotEmpty() && errors.isEmpty() ->
                    BooksRepository.SearchResult.Success(merged)

                merged.isNotEmpty() && errors.isNotEmpty() ->
                    BooksRepository.SearchResult.Partial(merged, errors)

                else ->
                    BooksRepository.SearchResult.Failure(errors.ifEmpty {
                        listOf(
                            IllegalStateException("No results")
                        )
                    })
            }
        }
    }

    private fun cacheKey(mode: String, query: String, limit: Int): String {
        val normalized = query.trim().lowercase().replace(Regex("""\s+"""), " ")
        return "$mode|$limit|$normalized"
    }

    private fun List<BookCandidate>.mergeAndRank(): List<BookCandidate> {
        if (isEmpty()) return emptyList()

        // 1) Group by best-available identity key
        val grouped: Map<String, List<BookCandidate>> = this.groupBy { it.dedupeKey() }

        // 2) Merge duplicates into one "best" candidate per key
        val merged: List<BookCandidate> = grouped.values.map { group ->
            group.reduce { acc, next -> acc.mergePreferBetter(next) }
        }

        // 3) Rank: prefer richer metadata
        return merged.sortedWith(
            comparator = compareByDescending<BookCandidate> { !it.isbn13.isNullOrBlank() }
                .thenByDescending { !it.isbn10.isNullOrBlank() }
                .thenByDescending { !it.coverUrl.isNullOrBlank() }
                .thenByDescending { it.publishedYear ?: 0 }
                .thenByDescending { it.authors.size }
                .thenByDescending { it.title.length }
        )
    }

    private fun BookCandidate.dedupeKey(): String {
        // strongest identifiers first
        isbn13?.takeIf { it.isNotBlank() }?.let { return "isbn13:${it.normalizeIsbn()}" }
        isbn10?.takeIf { it.isNotBlank() }?.let { return "isbn10:${it.normalizeIsbn()}" }

        // source-specific stable IDs
        if (sourceId.isNotBlank()) return "src:${source.name}:${sourceId.trim()}"

        // fallback: stable titleKey (title + first author + year)
        return "ta:${BookKeys.titleKey(title, authors, publishedYear)}"
    }

    private fun String.normalizeIsbn(): String =
        replace(oldValue = "-", newValue = "")
            .replace(oldValue = " ", newValue = "")
            .trim()

    private fun BookCandidate.mergePreferBetter(other: BookCandidate): BookCandidate {
        // Prefer non-null / richer fields.
        // Keep the original source/sourceId (doesn't matter much for merged display),
        // but we can keep the one that has "better" metadata as the base.
        val base = chooseBase(this, other)
        val extra = if (base === this) other else this

        return base.copy(
            // Merge best-known values
            publishedYear = base.publishedYear ?: extra.publishedYear,
            isbn13 = base.isbn13 ?: extra.isbn13,
            isbn10 = base.isbn10 ?: extra.isbn10,
            coverUrl = base.coverUrl ?: extra.coverUrl,
            authors = if (base.authors.size >= extra.authors.size) base.authors else extra.authors,
            title = if (base.title.length >= extra.title.length) base.title else extra.title
        )
    }

    private fun chooseBase(
        candidate1: BookCandidate,
        candidate2: BookCandidate,
    ): BookCandidate {

        // Pick the candidate with “better” metadata as base.
        fun score(candidate: BookCandidate): Int {
            var s = 0
            if (!candidate.isbn13.isNullOrBlank()) s += 8
            if (!candidate.isbn10.isNullOrBlank()) s += 4
            if (!candidate.coverUrl.isNullOrBlank()) s += 3
            if (candidate.publishedYear != null) s += 2
            s += (candidate.authors.size.coerceAtMost(maximumValue = 3)) // small boost
            return s
        }
        return if (score(candidate = candidate1) >= score(candidate = candidate2)) {
            candidate1
        } else {
            candidate2
        }
    }
}
