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

        // 1) de-dupe: ISBN-13 strongest, otherwise normalized (title+firstAuthor)
        val byIsbn = this.filter { !it.isbn13.isNullOrBlank() }
            .associateBy { it.isbn13!! }
            .toMutableMap()

        val noIsbn = this.filter { it.isbn13.isNullOrBlank() }

        val byKey = mutableMapOf<String, BookCandidate>()
        for (newCandidate in noIsbn) {
            val key = normalizeKey(newCandidate.title, newCandidate.authors.firstOrNull())
            val existing = byKey[key]
            byKey[key] = chooseBetter(existing, newCandidate)
        }

        // Combine: ISBN ones + non-ISBN ones that aren’t duplicates of ISBN group
        val combined = buildList {
            addAll(byIsbn.values)
            addAll(byKey.values)
        }

        // 2) rank: prefer ISBN-13, cover, year, more authors info
        return combined.sortedWith(
            compareByDescending<BookCandidate> { !it.isbn13.isNullOrBlank() }
                .thenByDescending { !it.coverUrl.isNullOrBlank() }
                .thenByDescending { it.publishedYear ?: 0 }
                .thenByDescending { it.authors.size }
                .thenBy { it.title.length } // slight preference for cleaner titles
        )
    }

    private fun normalizeKey(
        title: String,
        firstAuthor: String?,
    ): String {

        fun norm(s: String) = s.lowercase()
            .replace(Regex("""[^\p{L}\p{N}\s]"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()

        return norm(title) + "||" + norm(firstAuthor ?: "")
    }

    private fun chooseBetter(
        candidate1: BookCandidate?,
        candidate2: BookCandidate,
    ): BookCandidate {

        if (candidate1 == null) return candidate2
        val score1 = score(candidate1)
        val score2 = score(candidate2)
        return if (score2 > score1) candidate2 else candidate1
    }

    private fun score(
        candidate: BookCandidate,
    ): Int {

        var score = 0
        if (!candidate.isbn13.isNullOrBlank()) score += 10
        if (!candidate.coverUrl.isNullOrBlank()) score += 4
        if (candidate.publishedYear != null) score += 2
        if (candidate.authors.isNotEmpty()) score += 1
        return score
    }
}
