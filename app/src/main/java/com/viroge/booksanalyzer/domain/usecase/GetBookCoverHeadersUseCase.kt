package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.remote.BookCoverHeaders
import javax.inject.Inject

class GetBookCoverHeadersUseCase @Inject constructor(
    private val bookCoverHeaders: BookCoverHeaders,
) {

    operator fun invoke(url: String): Map<String, String> {
        return getCoverHeaders(url)
    }

    private fun getCoverHeaders(url: String): Map<String, String> = when {
        // Case 1: Open Library
        url.contains("openlibrary.org") -> bookCoverHeaders.getOpenLibraryHeaders()

        // Case 2: Google Books
        url.contains("google.com/books") || url.contains("googleapis.com") -> bookCoverHeaders.getGoogleBooksHeaders()

        else -> emptyMap()
    }
}
