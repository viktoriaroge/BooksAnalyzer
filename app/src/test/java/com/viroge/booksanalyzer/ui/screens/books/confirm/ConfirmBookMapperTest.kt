package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ConfirmBookMapperTest {
    private val mapper = ConfirmBookMapper()

    @Test
    fun `mapToDataState should format authors list into single string`() {
        val book = Book(
            id = "T",
            title = "Clean Code",
            authors = listOf("Author 1", "Author 2"),
            publishedYear = "2008",
            isbn13 = "9780132350884",
            source = BookSource.GOOGLE_BOOKS,
            coverUrl = "existing_url",
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )

        val result = mapper.mapToDataState(book)

        assertEquals("Author 1, Author 2", result.authors)
        assertEquals(R.string.book_source_full_google_books, result.sourceBadgeTextRes)
    }
}
