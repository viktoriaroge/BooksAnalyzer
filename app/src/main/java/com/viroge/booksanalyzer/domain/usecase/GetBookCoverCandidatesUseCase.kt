package com.viroge.booksanalyzer.domain.usecase

import android.util.Log
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import javax.inject.Inject

class GetBookCoverCandidatesUseCase @Inject constructor(
    private val getCoverHeaders: GetBookCoverHeadersUseCase,
) {

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

        val normalizedList = list.distinct()
        Log.println(Log.DEBUG, "CoverUrlOptimizer", "---> CoverCandidates: (${normalizedList.size}) $normalizedList")

        return normalizedList.map { attachCoverHeaders(url = it) }
    }

    fun attachCoverHeaders(url: String): BookCoverCandidate =
        BookCoverCandidate(url = url, headers = getCoverHeaders(url))

    private fun googleUpgrades(url: String): List<String> {
        val baseUrl = url.replace("http://", "https://")

        // Those are the most common zoom factors, try with the most optimal one first:
        return if (baseUrl.contains("zoom=")) listOf(
            baseUrl.replace(Regex("zoom=\\d+"), "zoom=3"),
            baseUrl.replace(Regex("zoom=\\d+"), "zoom=2"),
            baseUrl.replace(Regex("zoom=\\d+"), "zoom=1"),
        )
        else listOf(baseUrl)
    }

    private fun openLibraryUpgrades(url: String): List<String> {
        val baseUrl = url.replace("http://", "https://")

        // OpenLibrary covers: ...-S.jpg, ...-M.jpg, ...-L.jpg, ...-XL.jpg
        return listOf(
            baseUrl.replace(Regex("-(S|M|L|XL)\\.jpg$"), "-XL.jpg"),
            baseUrl.replace(Regex("-(S|M|L|XL)\\.jpg$"), "-L.jpg"),
            baseUrl.replace(Regex("-(S|M|L|XL)\\.jpg$"), "-M.jpg"),
        )
    }
}

data class BookCoverCandidate(
    val url: String,
    val headers: Map<String, String>,
)
