package com.viroge.booksanalyzer.data.remote.google

import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.TempBook
import com.viroge.booksanalyzer.ui.screens.books.BookTransitionKey
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

        val originalCoverUrl = (item.volumeInfo.imageLinks?.thumbnail ?: item.volumeInfo.imageLinks?.smallThumbnail)
            ?.replace(oldValue = "http://", newValue = "https://")

        val title = item.volumeInfo.title
        val authors = item.volumeInfo.authors
        val year = item.volumeInfo.publishedDate?.take(4)
        val source = BookSource.GOOGLE_BOOKS
        val sourceId = item.id

        return TempBook(
            animationKey = BookTransitionKey.calculate(title, authors, isbn13, source, sourceId),
            sourceId = sourceId,
            source = source,
            title = title,
            authors = item.volumeInfo.authors,
            year = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            originalCoverUrl = originalCoverUrl,
            coverUrl = coverUrl,
        )
    }
}
