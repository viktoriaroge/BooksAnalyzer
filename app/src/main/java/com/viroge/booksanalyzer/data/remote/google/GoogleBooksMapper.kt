package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.BuildConfig
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleBooksMapper @Inject constructor() {

    fun map(item: GoogleVolumeItem): TempBook {
        val isbn13 = item.volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                other = "ISBN_13", ignoreCase = true
            )
        }?.identifier

        val isbn10 = item.volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                other = "ISBN_10", ignoreCase = true
            )
        }?.identifier

        val year = item.volumeInfo.publishedDate?.take(4)
        val coverUrl = (item.volumeInfo.imageLinks?.thumbnail ?: item.volumeInfo.imageLinks?.smallThumbnail)?.replace(
            oldValue = "http://",
            newValue = "https://"
        )

        return TempBook(
            sourceId = item.id,
            source = BookSource.GOOGLE_BOOKS,
            title = item.volumeInfo.title,
            authors = item.volumeInfo.authors,
            year = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
        )
    }

    fun getBaseUrl(): String = "https://www.googleapis.com/books/v1/"

    fun getHeaders(): Map<String, String> = mapOf(
        "Accept" to "application/json",
        "X-Android-Package" to BuildConfig.APPLICATION_ID,
        "X-Android-Cert" to BuildConfig.DEBUG_SHA1.replace(oldValue = ":", newValue = "").lowercase(),
    )

    fun isUrlValid(url: String): Boolean = url.contains("google.com/books") || url.contains("googleapis.com")
}
