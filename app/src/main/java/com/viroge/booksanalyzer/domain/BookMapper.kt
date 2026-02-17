package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.remote.google.GoogleVolumeItem
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryDoc
import com.viroge.booksanalyzer.domain.BooksUtil.splitIsbns

object BookMapper {

    fun GoogleVolumeItem.toCandidate(): BookCandidate {
        val isbn13 = volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                other = "ISBN_13", ignoreCase = true
            )
        }?.identifier

        val isbn10 = volumeInfo.industryIdentifiers.firstOrNull {
            it.type.equals(
                other = "ISBN_10", ignoreCase = true
            )
        }?.identifier

        val year = volumeInfo.publishedDate?.take(4)?.toIntOrNull()
        val cover = (volumeInfo.imageLinks?.thumbnail
            ?: volumeInfo.imageLinks?.smallThumbnail)?.replace(
            oldValue = "http://",
            newValue = "https://",
        )

        return BookCandidate(
            source = BookSource.GOOGLE_BOOKS,
            sourceId = id,
            title = volumeInfo.title,
            authors = volumeInfo.authors,
            publishedYear = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = cover,
        )
    }

    fun OpenLibraryDoc.toCandidateOrNull(): BookCandidate? {
        val title = this.title?.takeIf { it.isNotBlank() } ?: return null
        val id = key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbns = isbn)

        val coverUrl = coverId?.let { coverId ->
            // Covers API: https://covers.openlibrary.org/b/id/{coverId}-{size}.jpg
            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
        }

        return BookCandidate(
            source = BookSource.OPEN_LIBRARY,
            sourceId = id,
            title = title,
            authors = authorName,
            publishedYear = firstPublishYear,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
        )
    }

    fun BookEntity.toBook(): Book = Book(
        id = this.bookId,
        source = when {
            this.googleVolumeId != null -> BookSource.GOOGLE_BOOKS
            this.openLibraryId != null -> BookSource.OPEN_LIBRARY
            else -> BookSource.MANUAL
        },
        sourceId = when {
            this.googleVolumeId != null -> this.googleVolumeId
            this.openLibraryId != null -> this.openLibraryId
            else -> this.bookId
        },
        status = when {
            this.status.equals(
                other = "NOT_STARTED",
                ignoreCase = true
            ) -> ReadingStatus.NOT_STARTED

            this.status.equals(other = "READING", ignoreCase = true) -> ReadingStatus.READING
            this.status.equals(other = "FINISHED", ignoreCase = true) -> ReadingStatus.FINISHED
            this.status.equals(other = "ABANDONED", ignoreCase = true) -> ReadingStatus.ABANDONED
            else -> ReadingStatus.NOT_STARTED
        },
        title = this.title,
        authors = this.authors.split(",").map { it.trim() },
        publishedYear = this.publishedYear,
        isbn13 = this.isbn13,
        isbn10 = this.isbn10,
        coverUrl = this.coverUrl,
        createdAtEpochMs = this.createdAtEpochMs,
        lastOpenAtEpochMs = this.lastOpenAtEpochMs,
    )
}