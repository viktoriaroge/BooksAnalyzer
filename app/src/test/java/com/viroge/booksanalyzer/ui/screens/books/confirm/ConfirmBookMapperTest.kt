package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ConfirmBookMapperTest {
    private val mapper = ConfirmBookMapper()

    @Test
    fun `mapToDataState should prioritize selected cover over book cover`() {
        val book = Book(
            id = "T",
            title = "Clean Code",
            authors = listOf("Robert C. Martin", "Micah Martin"),
            publishedYear = 2008,
            isbn13 = "9780132350884",
            source = BookSource.MANUAL,
            coverUrl = "existing_url",
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )
        val selectedUrl = "new_url"

        val result = mapper.mapToDataState(book, selectedUrl, emptyMap())

        assertEquals("new_url", result.coverUrl)
    }

    @Test
    fun `mapToDataState should format authors list into single string`() {
        val book = Book(
            id = "T",
            title = "Clean Code",
            authors = listOf("Author 1", "Author 2"),
            publishedYear = 2008,
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

        val result = mapper.mapToDataState(book, null, null)

        assertEquals("Author 1, Author 2", result.authors)
        assertEquals(R.string.book_source_full_google_books, result.sourceBadgeTextRes)
    }

    @Test
    fun `mapToManualFormData should correctly distribute query based on SearchMode`() {
        val query = "9781234567890"

        // Test ISBN mode
        val isbnResult = mapper.mapToManualFormData(query, SearchMode.ISBN)
        assertEquals("", isbnResult.initialTitle)
        assertEquals("", isbnResult.initialAuthors)
        assertEquals(query, isbnResult.initialIsbn13)

        // Test Author mode
        val authorQuery = "Robert Martin"
        val authorResult = mapper.mapToManualFormData(authorQuery, SearchMode.AUTHOR)
        assertEquals("", authorResult.initialTitle)
        assertEquals(authorQuery, authorResult.initialAuthors)
        assertEquals("", authorResult.initialIsbn13)

        // Test Title mode (or ALL)
        val titleResult = mapper.mapToManualFormData("Clean Code", SearchMode.TITLE)
        assertEquals("Clean Code", titleResult.initialTitle)
        assertEquals("", titleResult.initialAuthors)
    }
}
