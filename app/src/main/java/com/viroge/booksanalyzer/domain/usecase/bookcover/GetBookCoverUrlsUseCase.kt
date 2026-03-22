package com.viroge.booksanalyzer.domain.usecase.bookcover

import com.viroge.booksanalyzer.data.remote.google.GoogleBooksConfig
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryConfig
import javax.inject.Inject

class GetBookCoverUrlsUseCase @Inject constructor(
    private val googleBooksConfig: GoogleBooksConfig,
    private val openLibraryConfig: OpenLibraryConfig,
) {

    private val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)

    operator fun invoke(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ): List<String> {
        val candidates = getCoverCandidates(selectedCoverUrl, originalCoverUrl, isbn13)

        return if (containsEmpty(candidates)) candidates
        else candidates + "" // The generated url-s and finally an empty in the end
    }

    private fun containsEmpty(list: List<String>): Boolean = list.any { it.isEmpty() }

    private fun getCoverCandidates(
        selectedCoverUrl: String?,
        originalCoverUrl: String?,
        isbn13: String?,
    ): List<String> {
        val urls = mutableSetOf<String>()

        selectedCoverUrl?.let { selected -> if (selected.isNotBlank()) urls += selected }
        getUpgradedUrls(selectedCoverUrl?.trim()).also { urls += it }

        originalCoverUrl?.let { original -> if (original.isNotBlank()) urls += original }
        getUpgradedUrls(originalCoverUrl?.trim()).also { urls += it }

        // OpenLibrary by ISBN if not added already:
        isbn13?.trim()?.takeIf { it.isNotBlank() }?.let { isbn ->
            val suffixToFailBlank = "?default=false"
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-XL.jpg$suffixToFailBlank"
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg$suffixToFailBlank"
            urls += "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg$suffixToFailBlank"
        }

        return urls.toList()
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
