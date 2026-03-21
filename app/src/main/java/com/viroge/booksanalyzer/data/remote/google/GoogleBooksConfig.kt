package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleBooksConfig @Inject constructor() {

    val baseUrl = "https://www.googleapis.com/books/v1/"

    val headers = mapOf(
        "X-Android-Package" to BuildConfig.APPLICATION_ID,
        "X-Android-Cert" to BuildConfig.DEBUG_SHA1.replace(":", "").lowercase(),
    )

    fun isGoogleBooksRequest(url: String): Boolean = url.contains("googleapis.com") || url.contains("google.com/books")
}
