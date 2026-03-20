package com.viroge.booksanalyzer.domain.usecase.bookcover

import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
import javax.inject.Inject

class GetBookCoverCandidatesUseCase @Inject constructor() {

    private val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)

    operator fun invoke(
        coverUrl: String?,
        source: BookSource,
        isbn13: String?,
    ): List<BookCoverCandidate> {
        val candidates = getCoverCandidates(coverUrl, source, isbn13)

        return if (containsDefaultCover(candidates)) candidates
        else listOf(getDefaultCover()) + candidates
    }

    private fun containsDefaultCover(list: List<BookCoverCandidate>): Boolean = list.any { it.url.isEmpty() }

    private fun getDefaultCover(): BookCoverCandidate = BookCoverCandidate(url = "")

    /**
     * Get a list of cover candidates. Each candidate contains a pair of data:
     * First: the url, Second: a map of headers needed to load it.
     */
    private fun getCoverCandidates(
        coverUrl: String?,
        source: BookSource,
        isbn13: String?,
    ): List<BookCoverCandidate> {
        val list = mutableListOf<String>() // urls
        val url = coverUrl?.trim().orEmpty()

        if (url.isNotBlank()) {
            when (source) {
                BookSource.GOOGLE_BOOKS -> list += googleUpgrades(url)
                BookSource.OPEN_LIBRARY -> list += openLibraryUpgrades(url)
                BookSource.MANUAL -> {}
            }
        }

        // OpenLibrary by ISBN if not added already:
        isbn13?.trim()?.takeIf { it.isNotBlank() }?.let { isbn ->
            list += "https://covers.openlibrary.org/b/isbn/$isbn-XL.jpg"
            list += "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"
            list += "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg"
        }

        // Always include original at the end as fallback:
        coverUrl?.let { original -> if (original.isNotBlank()) list += original }

        return list.distinct().map { attachCoverHeaders(url = it) }
    }

    private fun attachCoverHeaders(url: String): BookCoverCandidate = BookCoverCandidate(url = url)

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
