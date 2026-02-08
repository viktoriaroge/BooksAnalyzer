package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.data.remote.NetworkErrorMapper
import com.viroge.booksanalyzer.domain.BookCandidate

class OpenLibraryClient(
    private val api: OpenLibraryApi,
) {

    suspend fun search(
        query: String,
        limit: Int = 10,
    ): Result<List<BookCandidate>> = runCatching {
        val resp = api.search(
            query = query,
            limit = limit,
        )
        resp.docs.mapNotNull { it.toCandidateOrNull() }
    }.mapError()

    private fun OpenLibraryDoc.toCandidateOrNull(): BookCandidate? {
        val title = this.title?.takeIf { it.isNotBlank() } ?: return null
        val id = key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbn)

        val coverUrl = coverId?.let { coverId ->
            // Covers API: https://covers.openlibrary.org/b/id/{coverId}-{size}.jpg
            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
        }

        return BookCandidate(
            source = BookCandidate.Source.OPEN_LIBRARY,
            sourceId = id,
            title = title,
            authors = authorName,
            publishedYear = firstPublishYear,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
        )
    }

    private fun splitIsbns(
        isbns: List<String>,
    ): Pair<String?, String?> {
        val isbn13 = isbns.firstOrNull { it.length == 13 }
        val isbn10 = isbns.firstOrNull { it.length == 10 }
        return isbn13 to isbn10
    }

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(NetworkErrorMapper.map(it)) })
}
