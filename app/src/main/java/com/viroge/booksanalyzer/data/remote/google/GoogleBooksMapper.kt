package com.viroge.booksanalyzer.data.remote.google

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
        val coverUrl = getGoogleBooksHighResCover(item.id)

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

    fun getGoogleBooksHighResCover(volumeId: String, width: Int = 800): String {
        // Use 'fife' to request a specific width and NO height so it maintains aspect ratio:
        return "https://books.google.com/books/publisher/content/images/frontcover/$volumeId?fife=w$width"
    }
}
