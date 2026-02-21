package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.BuildConfig

object BookHeaders {

    fun getOpenLibraryHeaders(): Map<String, String> = mapOf(
        "Accept" to "application/json",
        "User-Agent" to "BooksAnalyzerApp (${BuildConfig.USER_EMAIL})",
    )

    fun getGoogleBooksHeaders(): Map<String, String> = mapOf(
        "Accept" to "application/json",
        "X-Android-Package" to BuildConfig.APPLICATION_ID,
        "X-Android-Cert" to BuildConfig.DEBUG_SHA1.replace(oldValue = ":", newValue = "").lowercase(),
    )
}