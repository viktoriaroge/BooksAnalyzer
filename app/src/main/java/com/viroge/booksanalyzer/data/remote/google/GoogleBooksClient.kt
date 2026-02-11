package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.data.remote.NetworkErrorMapper
import com.viroge.booksanalyzer.domain.BookCandidate

class GoogleBooksClient(
    private val api: GoogleBooksApi,
    private val apiKey: String,
) {

    suspend fun search(
        query: String,
        limit: Int = 10,
        startIndex: Int = 0,
    ): Result<List<BookCandidate>> = runCatching {
        val resp = api.searchVolumes(
            query = query,
            startIndex = startIndex,
            maxResults = limit,
            apiKey = apiKey,
        )
        resp.items.map { it.toCandidate() }
    }.mapError()

    private fun VolumeItem.toCandidate(): BookCandidate {
        val isbn13 = volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                "ISBN_13", true
            )
        }?.identifier

        val isbn10 = volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                "ISBN_10", true
            )
        }?.identifier

        val year = volumeInfo.publishedDate?.take(4)?.toIntOrNull()
        val cover = (volumeInfo.imageLinks?.thumbnail
            ?: volumeInfo.imageLinks?.smallThumbnail)?.replace("http://", "https://")

        return BookCandidate(
            source = BookCandidate.Source.GOOGLE_BOOKS,
            sourceId = id,
            title = volumeInfo.title,
            authors = volumeInfo.authors,
            publishedYear = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = cover,
        )
    }

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(NetworkErrorMapper.map(it)) })
}
