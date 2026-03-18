package com.viroge.booksanalyzer.data.local

import com.viroge.booksanalyzer.data.local.books.BookEntity
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksEntityMapper @Inject constructor() {

    fun map(entity: BookEntity, headers: Map<String, String>): Book = Book(
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
        originalCoverUrl = entity.originalCoverUrl,
        coverUrl = entity.coverUrl,
        coverRequestHeaders = headers,
        createdAtEpochMs = entity.createdAtEpochMs,
        lastOpenAtEpochMs = entity.lastOpenAtEpochMs,
        lastMarkedToDelete = entity.lastMarkedToDelete,
        toBeDeleted = entity.toBeDeleted,
    )
}
