package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.remote.BookCoverHeaders
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBookCoverHeadersUseCaseTest {

    private val bookCoverHeaders = mockk<BookCoverHeaders>()
    private lateinit var useCase: GetBookCoverHeadersUseCase

    private val googleHeaders = mapOf("Authorization" to "Bearer google_token")
    private val openLibraryHeaders = mapOf("User-Agent" to "OpenLibrary-App")

    @Before
    fun setup() {
        useCase = GetBookCoverHeadersUseCase(bookCoverHeaders)

        every { bookCoverHeaders.getGoogleBooksHeaders() } returns googleHeaders
        every { bookCoverHeaders.getOpenLibraryHeaders() } returns openLibraryHeaders
    }

    @Test
    fun `when url is Open Library, should return open library headers`() {
        val url = "https://openlibrary.org/b/id/123-L.jpg"

        val result = useCase(url)

        assertEquals(openLibraryHeaders, result)
    }

    @Test
    fun `when url is Google Books (books dot com), should return google headers`() {
        val url = "https://books.google.com/books/content?id=123"

        val result = useCase(url)

        assertEquals(googleHeaders, result)
    }

    @Test
    fun `when url is Google APIs, should return google headers`() {
        val url = "https://books.googleapis.com/books/v1/volumes/123"

        val result = useCase(url)

        assertEquals(googleHeaders, result)
    }

    @Test
    fun `when url is unknown, should return empty map`() {
        val url = "https://some-other-source.com/image.png"

        val result = useCase(url)

        assertEquals(emptyMap<String, String>(), result)
    }

    @Test
    fun `when url contains keywords in middle of string, should still match`() {
        // Just checking the contains() logic is robust
        val url = "https://proxy.com?target=openlibrary.org/item"

        val result = useCase(url)

        assertEquals(openLibraryHeaders, result)
    }
}
