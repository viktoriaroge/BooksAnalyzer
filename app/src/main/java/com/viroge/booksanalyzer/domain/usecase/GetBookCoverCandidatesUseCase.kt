package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import javax.inject.Inject

class GetBookCoverCandidatesUseCase @Inject constructor(
    private val getCoverHeaders: GetBookCoverHeadersUseCase,
) {

    private val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)

    operator fun invoke(book: Book): List<BookCoverCandidate> {
        val candidates = getCoverCandidates(book)

        return if (containsDefaultCover(candidates)) candidates
        else listOf(getDefaultCover()) + candidates
    }

    private fun containsDefaultCover(list: List<BookCoverCandidate>): Boolean = list.any {
        it.url.isEmpty() && it.headers.isEmpty()
    }

    private fun getDefaultCover(): BookCoverCandidate = BookCoverCandidate(
        url = "",
        headers = emptyMap(),
    )

    /**
     * Get a list of cover candidates. Each candidate contains a pair of data:
     * First: the url, Second: a map of headers needed to load it.
     */
    private fun getCoverCandidates(book: Book): List<BookCoverCandidate> {
        val list = mutableListOf<String>() // urls
        val url = book.coverUrl?.trim().orEmpty()

        if (url.isNotBlank()) {
            when (book.source) {
                BookSource.GOOGLE_BOOKS -> list += googleUpgrades(url)
                BookSource.OPEN_LIBRARY -> list += openLibraryUpgrades(url)
                BookSource.MANUAL -> {}
            }
        }

        // OpenLibrary by ISBN if not added already:
        book.isbn13?.trim()?.takeIf { it.isNotBlank() }?.let { isbn ->
            list += "https://covers.openlibrary.org/b/isbn/$isbn-XL.jpg"
            list += "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"
            list += "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg"
        }

        // Always include original at the end as fallback:
        book.coverUrl?.let { original -> if (original.isNotBlank()) list += original }

        return list.distinct().map { attachCoverHeaders(url = it) }
    }

    private fun attachCoverHeaders(url: String): BookCoverCandidate =
        BookCoverCandidate(url = url, headers = getCoverHeaders(url))

    private fun googleUpgrades(url: String): List<String> {
        // Upgrades only if it starts with http:// (case-insensitive)
        val baseUrl = url.replace(protocolRegex, "https://")

        return if (baseUrl.contains("zoom=")) {
            listOf("3", "2", "1").map { level ->
                baseUrl.replace(Regex("zoom=\\d+"), "zoom=$level")
            }
        } else {
            listOf(baseUrl)
        }
    }

    private fun openLibraryUpgrades(url: String): List<String> {
        val baseUrl = url.replace(protocolRegex, "https://")

        // Defines the sizes in order of preference
        return listOf("-XL.jpg", "-L.jpg", "-M.jpg").map { size ->
            baseUrl.replace(Regex("-(S|M|L|XL)\\.jpg$"), size)
        }
    }
}

data class BookCoverCandidate(
    val url: String,
    val headers: Map<String, String>,
)
