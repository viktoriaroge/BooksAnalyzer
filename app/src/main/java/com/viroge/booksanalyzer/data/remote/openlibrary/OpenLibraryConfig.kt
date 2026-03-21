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
}
