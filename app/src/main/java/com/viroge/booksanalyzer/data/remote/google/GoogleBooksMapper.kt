package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleBooksMapper @Inject constructor() {

    fun map(item: GoogleVolumeItem, coverUrl: String?): TempBook {
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

        val originalCoverUrl = (item.volumeInfo.imageLinks?.thumbnail ?: item.volumeInfo.imageLinks?.smallThumbnail)
            ?.replace(oldValue = "http://", newValue = "https://")

        return TempBook(
            sourceId = item.id,
            source = BookSource.GOOGLE_BOOKS,
            title = item.volumeInfo.title,
            authors = item.volumeInfo.authors,
            year = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            originalCoverUrl = originalCoverUrl,
            coverUrl = coverUrl,
        )
    }
}
