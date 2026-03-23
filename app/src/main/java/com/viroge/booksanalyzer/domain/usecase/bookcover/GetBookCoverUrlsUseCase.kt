package com.viroge.booksanalyzer.domain.usecase.bookcover

import com.viroge.booksanalyzer.data.remote.google.GoogleBooksConfig
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryConfig
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryCoverSize
import com.viroge.booksanalyzer.domain.model.BookCoverDataSeed
import com.viroge.booksanalyzer.domain.model.BookSource
import javax.inject.Inject

class GetBookCoverUrlsUseCase @Inject constructor(
    private val googleBooksConfig: GoogleBooksConfig,
    private val openLibraryConfig: OpenLibraryConfig,
) {

    operator fun invoke(
        seed: BookCoverDataSeed,
    ): List<String> {
        val candidates = getCoverCandidates(seed)

        return if (containsEmpty(candidates)) candidates
        else candidates + "" // The generated url-s and finally an empty in the end
    }

    private fun containsEmpty(list: List<String>): Boolean = list.any { it.isEmpty() }

    private fun getCoverCandidates(
        seed: BookCoverDataSeed,
    ): List<String> {
        val urls = mutableSetOf<String>()

        seed.selectedCoverUrl?.let { selected -> if (selected.isNotBlank()) urls += selected }
        getUpgradedUrls(seed.selectedCoverUrl?.trim()).also { urls += it }

        seed.originalCoverUrl?.let { original -> if (original.isNotBlank()) urls += original }
        getUpgradedUrls(seed.originalCoverUrl?.trim()).also { urls += it }

        // GoogleBooks by sourceId if not added already:
        when (seed.source) {
            BookSource.GOOGLE_BOOKS -> {
                seed.sourceId?.let { id ->
                    googleBooksConfig.getCoverUrl(id).also { urls += it }
                }
            }

            BookSource.OPEN_LIBRARY, BookSource.MANUAL -> {
                // No op
            }
        }

        // OpenLibrary by ISBN if not added already:
        seed.isbn13?.trim()?.takeIf { it.isNotBlank() }?.let { isbn ->
            urls += openLibraryConfig.getCoverUrl(coverId = isbn, imageSize = OpenLibraryCoverSize.XL)
            urls += openLibraryConfig.getCoverUrl(coverId = isbn, imageSize = OpenLibraryCoverSize.L)
            urls += openLibraryConfig.getCoverUrl(coverId = isbn, imageSize = OpenLibraryCoverSize.M)
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
        val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)
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
        val protocolRegex = Regex("^http://", RegexOption.IGNORE_CASE)
        val baseUrl = url.replace(protocolRegex, "https://")

        // Defines the sizes in order of preference
        val urls = listOf("-XL.jpg", "-L.jpg", "-M.jpg")
            .map { size -> baseUrl.replace(Regex("-(S|M|L|XL)\\.jpg"), size) }
        return urls
    }
}
