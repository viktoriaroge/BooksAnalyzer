package com.viroge.booksanalyzer.domain.usecase.bookcover

import com.viroge.booksanalyzer.data.remote.google.GoogleBooksConfig
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryConfig
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
import javax.inject.Inject

class GetBookCoverCandidatesUseCase @Inject constructor(
    private val googleBooksConfig: GoogleBooksConfig,
    private val openLibraryConfig: OpenLibraryConfig,
) {

    private val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)

    operator fun invoke(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ): List<BookCoverCandidate> {
        val candidates = getCoverCandidates(selectedCoverUrl, originalCoverUrl, isbn13)

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
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ): List<BookCoverCandidate> {
        val urls = mutableSetOf<String>()

        selectedCoverUrl?.let { selected -> if (selected.isNotBlank()) urls += selected }
        getUpgradedUrls(selectedCoverUrl?.trim()).also { urls += it }

        originalCoverUrl?.let { original -> if (original.isNotBlank()) urls += original }
        getUpgradedUrls(originalCoverUrl?.trim()).also { urls += it }

        // OpenLibrary by ISBN if not added already:
        isbn13?.trim()?.takeIf { it.isNotBlank() }?.let { isbn ->
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-XL.jpg"
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg"
        }

        return urls.map { attachCoverHeaders(url = it) }
    }

    private fun getUpgradedUrls(url: String?): Set<String> {
        val urls = mutableSetOf<String>()
        url?.let {
            if (googleBooksConfig.isGoogleBooksRequest(it)) {
                urls += googleUpgrades(it)
            }
            if (openLibraryConfig.isOpenLibraryRequest(it)) {
                urls += openLibraryUpgrades(it)
            }
        }
        return urls
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
