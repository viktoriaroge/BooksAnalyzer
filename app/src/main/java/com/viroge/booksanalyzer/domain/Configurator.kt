package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.BuildConfig

class Configurator {

    fun getGoogleBooksApiKey(): String = BuildConfig.GOOGLE_BOOKS_API_KEY
    fun getUserEmail(): String = BuildConfig.USER_EMAIL
}