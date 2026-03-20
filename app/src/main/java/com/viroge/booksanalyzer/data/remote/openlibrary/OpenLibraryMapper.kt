package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.BuildConfig
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenLibraryMapper @Inject constructor() {

    fun mapOrNull(doc: OpenLibraryDoc): TempBook? {
        val title = doc.title?.takeIf { it.isNotBlank() } ?: return null
        val id = doc.key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbns = doc.isbn)

        val coverUrl = doc.coverId?.let { coverId ->
            // Covers API: https://covers.openlibrary.org/b/id/{coverId}-{size}.jpg
            // We have the following options: S, M, L, XL
            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
        }

        return TempBook(
            sourceId = id,
            source = BookSource.OPEN_LIBRARY,
            title = title,
            authors = doc.authorName,
            year = doc.firstPublishYear?.toString(),
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
        )
    }

    fun getBaseUrl(): String = "https://openlibrary.org/"

    fun getHeaders(): Map<String, String> = mapOf(
        "Accept" to "application/json",
        "User-Agent" to "BooksAnalyzerApp (${BuildConfig.USER_EMAIL})",
    )

    fun isUrlValid(url: String): Boolean = url.contains("openlibrary.org")

    private fun splitIsbns(
        isbns: List<String>,
    ): Pair<String?, String?> {
        val isbn13 = isbns.firstOrNull { it.length == 13 }
        val isbn10 = isbns.firstOrNull { it.length == 10 }
        return isbn13 to isbn10
    }
}
