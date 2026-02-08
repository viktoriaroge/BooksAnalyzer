package com.viroge.booksanalyzer.data

import androidx.collection.LruCache
import com.viroge.booksanalyzer.data.remote.google.GoogleBooksClient
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryClient
import com.viroge.booksanalyzer.domain.BookCandidate
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookSearchRepositoryImpl @Inject constructor(
    private val google: GoogleBooksClient,
    private val openLibrary: OpenLibraryClient,
) : BookSearchRepository {

    private val resultsCache = LruCache<String, List<BookCandidate>>(/*maxSize*/ 100)

    override suspend fun search(
        query: String,
    ): BookSearchRepository.SearchResult = multiSourceSearch(
        cacheKey = cacheKey(mode = "q", query = query, limit = 15),
        query = query,
        limit = 15,
    )

    override suspend fun lookupByIsbn(
        isbn: String,
    ): BookSearchRepository.SearchResult = multiSourceSearch(
        cacheKey = cacheKey(mode = "isbn", query = isbn, limit = 10),
        query = "isbn:$isbn",
        limit = 10,
    )

    private suspend fun multiSourceSearch(
        cacheKey: String,
        query: String,
        limit: Int,
    ): BookSearchRepository.SearchResult {

        // 1) Serve from cache
        resultsCache[cacheKey]?.let { cached ->
            return BookSearchRepository.SearchResult.Success(cached)
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
                    BookSearchRepository.SearchResult.Success(merged)

                merged.isNotEmpty() && errors.isNotEmpty() ->
                    BookSearchRepository.SearchResult.Partial(merged, errors)

                else ->
                    BookSearchRepository.SearchResult.Failure(errors.ifEmpty {
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
