package com.viroge.booksanalyzer.domain

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverUrlOptimizer @Inject constructor() {

    fun getCoverCandidates(book: Book): List<String> {
        val list = mutableListOf<String>()
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
            val xLargeUrl = "https://covers.openlibrary.org/b/isbn/$isbn-XL.jpg"
            if (!list.contains(xLargeUrl)) list += xLargeUrl

            val largeUrl = "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"
            if (!list.contains(largeUrl)) list += largeUrl

            val mediumUrl = "https://covers.openlibrary.org/b/isbn/$isbn-M.jpg"
            if (!list.contains(mediumUrl)) list += mediumUrl
        }

        // Always include original at the end as fallback:
        book.coverUrl?.let { original ->
            if (original.isNotBlank()) list += original
        }

        Log.println(Log.DEBUG, "CoverUrlOptimizer", "---> CoverCandidates: (${list.size}) $list")

        return list
    }

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