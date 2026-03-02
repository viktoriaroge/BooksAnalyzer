package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.remote.google.GoogleVolumeItem
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryDoc
import com.viroge.booksanalyzer.domain.BooksUtil.splitIsbns
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus

object BookMapper {

    fun GoogleVolumeItem.toBook(): Book {
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
        val coverUrl = (volumeInfo.imageLinks?.thumbnail ?: volumeInfo.imageLinks?.smallThumbnail)?.replace(
            oldValue = "http://",
            newValue = "https://"
        )

        return Book(
            id = "", // not important for network construction
            sourceId = id,
            source = BookSource.GOOGLE_BOOKS,
            title = volumeInfo.title,
            authors = volumeInfo.authors,
            publishedYear = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
            coverRequestHeaders = CoverUrlOptimizer.getCoverHeaders(url = coverUrl),
            status = ReadingStatus.NOT_STARTED,
            createdAtEpochMs = 0, // not important for network construction
            lastOpenAtEpochMs = 0, // not important for network construction
            lastMarkedToDelete = 0, // not important for network construction
            toBeDeleted = false, // not important for network construction
        )
    }

    fun OpenLibraryDoc.toBookOrNull(): Book? {
        val title = this.title?.takeIf { it.isNotBlank() } ?: return null
        val id = key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbns = isbn)

        val coverUrl = coverId?.let { coverId ->
            // Covers API: https://covers.openlibrary.org/b/id/{coverId}-{size}.jpg
            // We have the following options: S, M, L, XL
            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
        }

        return Book(
            id = "", // not important for network construction
            sourceId = id,
            source = BookSource.OPEN_LIBRARY,
            title = title,
            authors = authorName,
            publishedYear = firstPublishYear,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
            coverRequestHeaders = CoverUrlOptimizer.getCoverHeaders(url = coverUrl),
            status = ReadingStatus.NOT_STARTED,
            createdAtEpochMs = 0, // not important for network construction
            lastOpenAtEpochMs = 0, // not important for network construction
            lastMarkedToDelete = 0, // not important for network construction
            toBeDeleted = false, // not important for network construction
        )
    }

    fun getBookFromManualInput(
        title: String,
        authors: String,
        publishedYear: Int?,
        isbn13: String?,
        coverUrl: String?,
    ): Book = Book(
        id = "", // not important for network construction
        source = BookSource.MANUAL,
        sourceId = null,
        title = title,
        authors = authors.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("") },
        publishedYear = publishedYear,
        isbn13 = isbn13?.takeIf { it.isNotBlank() },
        isbn10 = null,
        coverUrl = coverUrl?.takeIf { it.isNotBlank() },
        coverRequestHeaders = CoverUrlOptimizer.getCoverHeaders(url = coverUrl),
        status = ReadingStatus.NOT_STARTED,
        createdAtEpochMs = 0, // not important for network construction
        lastOpenAtEpochMs = 0, // not important for network construction
        lastMarkedToDelete = 0, // not important for network construction
        toBeDeleted = false, // not important for network construction
    )

    fun BookEntity.toBook(): Book = Book(
        id = this.bookId,
        sourceId = this.sourceId,
        source = when {
            this.source.equals(other = "GOOGLE_BOOKS", ignoreCase = true) -> BookSource.GOOGLE_BOOKS
            this.source.equals(other = "OPEN_LIBRARY", ignoreCase = true) -> BookSource.OPEN_LIBRARY

            else -> BookSource.MANUAL
        },
        status = when {
            this.status.equals(
                other = "NOT_STARTED", ignoreCase = true
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
        coverRequestHeaders = CoverUrlOptimizer.getCoverHeaders(url = coverUrl),
        createdAtEpochMs = this.createdAtEpochMs,
        lastOpenAtEpochMs = this.lastOpenAtEpochMs,
        lastMarkedToDelete = this.lastMarkedToDelete,
        toBeDeleted = this.toBeDeleted,
    )
}