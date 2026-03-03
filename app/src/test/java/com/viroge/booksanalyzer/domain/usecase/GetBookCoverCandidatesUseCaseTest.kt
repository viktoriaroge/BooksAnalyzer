package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class GetBookCoverCandidatesUseCaseTest {

    private val getCoverHeaders = mockk<GetBookCoverHeadersUseCase>()
    private lateinit var useCase: GetBookCoverCandidatesUseCase

    @Before
    fun setup() {
        useCase = GetBookCoverCandidatesUseCase(getCoverHeaders)
        // Mock headers to return empty for simplicity
        every { getCoverHeaders(any()) } returns emptyMap()
    }

    @Test
    fun `should always return default empty cover at the first position`() {
        val book = Book(
            id = "1234",
            coverUrl = null,
            isbn13 = null,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )

        val result = useCase(book)

        assertEquals(1, result.size)
        assertTrue(result[0].url.isEmpty())
        assertTrue(result[0].headers.isEmpty())
    }

    @Test
    fun `given Google Books url, should generate upgraded zoom candidates`() {
        val originalUrl = "https://books.google.com/books?id=123&zoom=1"
        val book = Book(
            id = "1234",
            coverUrl = originalUrl,
            isbn13 = null,
            source = BookSource.GOOGLE_BOOKS,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )

        val result = useCase(book)

        // Index 0: Default
        // Index 1-3: zoom=3, zoom=2, zoom=1 (upgrades)
        // Index 4: originalUrl (distinct will catch this as duplicate of result[3])

        val urls = result.map { it.url }
        assertTrue(urls.contains("https://books.google.com/books?id=123&zoom=3"))
        assertTrue(urls.contains("https://books.google.com/books?id=123&zoom=2"))
        assertEquals(originalUrl, urls.last())
    }

    @Test
    fun `given ISBN, should include OpenLibrary ISBN candidates`() {
        val isbn = "1234567890123"
        val book = Book(
            id = "1234",
            coverUrl = null,
            isbn13 = isbn,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )

        val result = useCase(book)
        val urls = result.map { it.url }

        assertTrue(urls.any { it.contains("isbn/$isbn-XL.jpg") })
        assertTrue(urls.any { it.contains("isbn/$isbn-L.jpg") })
    }

    @Test
    fun `should remove duplicate urls while preserving order`() {
        val url = "https://test.com/cover.jpg"
        // Book where original URL matches an upgrade or happens twice
        val book = Book(
            id = "1234",
            coverUrl = url,
            isbn13 = null,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )

        val result = useCase(book)

        // Result should be: [Default, Original]
        assertEquals(2, result.size)
        assertEquals("", result[0].url)
        assertEquals(url, result[1].url)
    }

    @Test
    fun `should call header usecase for every candidate url`() {
        val url = "https://custom.com/image.jpg"
        val book = Book(
            id = "1234",
            coverUrl = url,
            isbn13 = null,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
            lastMarkedToDelete = 0,
            toBeDeleted = false,
        )
        val mockHeaders = mapOf("Key" to "Value")

        every { getCoverHeaders(url) } returns mockHeaders

        val result = useCase(book)
        val candidate = result.find { it.url == url }

        assertEquals(mockHeaders, candidate?.headers)
    }
}
