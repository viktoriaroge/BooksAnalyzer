package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.domain.BookHeaders.getGoogleBooksHeaders
import com.viroge.booksanalyzer.domain.BookHeaders.getOpenLibraryHeaders
import javax.inject.Inject

class GetBookCoverHeadersUseCase @Inject constructor() {

    operator fun invoke(url: String): Map<String, String> {
        return getCoverHeaders(url)
    }

    private fun getCoverHeaders(url: String?): Map<String, String> = when {
        url == null -> emptyMap()

        // Case 1: Open Library
        url.contains("openlibrary.org") -> getOpenLibraryHeaders()

        // Case 2: Google Books
        url.contains("google.com/books") || url.contains("googleapis.com") -> getGoogleBooksHeaders()

        else -> emptyMap()
    }
}
