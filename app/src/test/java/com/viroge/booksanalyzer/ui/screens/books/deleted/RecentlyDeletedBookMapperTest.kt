package com.viroge.booksanalyzer.ui.screens.books.deleted

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentlyDeletedBookMapperTest {

    private val mapper = RecentlyDeletedBookMapper()

    @Test
    fun `getScreenValues returns correct resource constants`() {
        val values = mapper.getScreenValues()

        assertEquals(R.string.recently_deleted_screen_name, values.screenName)
        assertEquals(R.string.recently_deleted_screen_source_label, values.sourceLabel)
    }

    @Test
    fun `map should format authors and metadata correctly`() {
        val book = Book(
            id = "1",
            title = "Clean Code",
            authors = listOf("Robert C. Martin", "Micah Martin"),
            publishedYear = "2008",
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

        val result = mapper.map(listOf(book)).first()

        assertEquals("Robert C. Martin, Micah Martin", result.authors)
        assertEquals("2008 • 9780132350884", result.metadata)
    }

    @Test
    fun `map should handle missing metadata gracefully`() {
        val book = Book(
            id = "2",
            title = "Unknown Book",
            authors = emptyList(),
            publishedYear = null,
            isbn13 = null,
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

        val result = mapper.map(listOf(book)).first()

        assertEquals("", result.authors)
        assertEquals("", result.metadata)
    }
}
