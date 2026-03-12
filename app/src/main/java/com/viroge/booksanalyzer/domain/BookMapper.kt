package com.viroge.booksanalyzer.domain

import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.data.remote.BookCoverHeaders
import com.viroge.booksanalyzer.data.remote.google.GoogleVolumeItem
import com.viroge.booksanalyzer.data.remote.openlibrary.OpenLibraryDoc
import com.viroge.booksanalyzer.domain.BooksUtil.splitIsbns
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookMapper @Inject constructor(
    private val bookCoverHeaders: BookCoverHeaders,
) {

    fun map(item: GoogleVolumeItem): Book {
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

        return Book(
            id = "", // not important for network construction
            sourceId = item.id,
            source = BookSource.GOOGLE_BOOKS,
            title = item.volumeInfo.title,
            authors = item.volumeInfo.authors,
            publishedYear = year,
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
            coverRequestHeaders = getCoverHeaders(url = coverUrl),
            status = ReadingStatus.NOT_STARTED,
            createdAtEpochMs = 0, // not important for network construction
            lastOpenAtEpochMs = 0, // not important for network construction
            lastMarkedToDelete = 0, // not important for network construction
            toBeDeleted = false, // not important for network construction
        )
    }

    fun mapOrNull(doc: OpenLibraryDoc): Book? {
        val title = doc.title?.takeIf { it.isNotBlank() } ?: return null
        val id = doc.key ?: return null

        val (isbn13, isbn10) = splitIsbns(isbns = doc.isbn)

        val coverUrl = doc.coverId?.let { coverId ->
            // Covers API: https://covers.openlibrary.org/b/id/{coverId}-{size}.jpg
            // We have the following options: S, M, L, XL
            "https://covers.openlibrary.org/b/id/$coverId-L.jpg"
        }

        return Book(
            id = "", // not important for network construction
            sourceId = id,
            source = BookSource.OPEN_LIBRARY,
            title = title,
            authors = doc.authorName,
            publishedYear = doc.firstPublishYear?.toString(),
            isbn13 = isbn13,
            isbn10 = isbn10,
            coverUrl = coverUrl,
            coverRequestHeaders = getCoverHeaders(url = coverUrl),
            status = ReadingStatus.NOT_STARTED,
            createdAtEpochMs = 0, // not important for network construction
            lastOpenAtEpochMs = 0, // not important for network construction
            lastMarkedToDelete = 0, // not important for network construction
            toBeDeleted = false, // not important for network construction
        )
    }

    fun mapFromManualInput(
        title: String,
        authors: String,
        year: String?,
        isbn13: String?,
    ): Book = Book(
        id = "", // not important for network construction
        source = BookSource.MANUAL,
        sourceId = null,
        title = title,
        authors = authors.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("") },
        publishedYear = year,
        isbn13 = isbn13?.takeIf { it.isNotBlank() },
        isbn10 = null,
        coverUrl = null,
        coverRequestHeaders = emptyMap(),
        status = ReadingStatus.NOT_STARTED,
        createdAtEpochMs = 0, // not important for network construction
        lastOpenAtEpochMs = 0, // not important for network construction
        lastMarkedToDelete = 0, // not important for network construction
        toBeDeleted = false, // not important for network construction
    )

    fun map(entity: BookEntity): Book = Book(
        id = entity.bookId,
        sourceId = entity.sourceId,
        source = when {
            entity.source.equals(other = "GOOGLE_BOOKS", ignoreCase = true) -> BookSource.GOOGLE_BOOKS
            entity.source.equals(other = "OPEN_LIBRARY", ignoreCase = true) -> BookSource.OPEN_LIBRARY

            else -> BookSource.MANUAL
        },
        status = when {
            entity.status.equals(
                other = "NOT_STARTED", ignoreCase = true
            ) -> ReadingStatus.NOT_STARTED

            entity.status.equals(other = "READING", ignoreCase = true) -> ReadingStatus.READING
            entity.status.equals(other = "FINISHED", ignoreCase = true) -> ReadingStatus.FINISHED
            entity.status.equals(other = "ABANDONED", ignoreCase = true) -> ReadingStatus.ABANDONED
            else -> ReadingStatus.NOT_STARTED
        },
        title = entity.title,
        authors = entity.authors.split(",").map { it.trim() },
        publishedYear = entity.publishedYear,
        isbn13 = entity.isbn13,
        isbn10 = entity.isbn10,
        coverUrl = entity.coverUrl,
        coverRequestHeaders = getCoverHeaders(url = entity.coverUrl),
        createdAtEpochMs = entity.createdAtEpochMs,
        lastOpenAtEpochMs = entity.lastOpenAtEpochMs,
        lastMarkedToDelete = entity.lastMarkedToDelete,
        toBeDeleted = entity.toBeDeleted,
    )

    @Deprecated("Use GetBookCoverHeadersUseCase instead")
    fun getCoverHeaders(url: String?): Map<String, String> = when {
        url == null -> emptyMap()

        // Case 1: Open Library
        url.contains("openlibrary.org") -> bookCoverHeaders.getOpenLibraryHeaders()

        // Case 2: Google Books
        url.contains("google.com/books") || url.contains("googleapis.com") -> bookCoverHeaders.getGoogleBooksHeaders()

        else -> emptyMap()
    }
}