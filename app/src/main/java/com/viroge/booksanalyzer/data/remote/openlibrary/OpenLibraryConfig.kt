package com.viroge.booksanalyzer.data.remote.openlibrary

import com.viroge.booksanalyzer.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenLibraryConfig @Inject constructor() {

    val baseUrl = "https://openlibrary.org/"

    val headers = mapOf(
        "User-Agent" to "BooksAnalyzerApp (${BuildConfig.USER_EMAIL})",
    )

    fun isOpenLibraryRequest(url: String): Boolean = url.contains("openlibrary.org")

    fun getCoverUrl(
        coverId: String,
        imageSize: OpenLibraryCoverSize = OpenLibraryCoverSize.L,
    ): String {
        val suffixToFailBlank = "?default=false"
        return "https://covers.openlibrary.org/b/id/$coverId-${imageSize.name}.jpg$suffixToFailBlank"
    }
}

enum class OpenLibraryCoverSize {
    S, M, L, XL // Keep names exactly as is or map them if renaming is ever required
}
