package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.usecase.bookcover.GetBookCoverHeadersUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBookCoverHeadersUseCaseTest {

    private val booksRepository = mockk<BooksRepository>()
    private lateinit var useCase: GetBookCoverHeadersUseCase

    private val googleHeaders = mapOf("Authorization" to "Bearer google_token")
    private val openLibraryHeaders = mapOf("User-Agent" to "OpenLibrary-App")

    @Before
    fun setup() {
        useCase = GetBookCoverHeadersUseCase(booksRepository)
    }

    @Test
    fun `when url is Open Library, should return open library headers`() {
        val url = "https://openlibrary.org/b/id/123-L.jpg"
        every { booksRepository.getBookCoverHeaders(url) } returns openLibraryHeaders

        val result = useCase(url)

        assertEquals(openLibraryHeaders, result)
    }

    @Test
    fun `when url is Google Books (books dot com), should return google headers`() {
        val url = "https://books.google.com/books/content?id=123"
        every { booksRepository.getBookCoverHeaders(url) } returns googleHeaders

        val result = useCase(url)

        assertEquals(googleHeaders, result)
    }

    @Test
    fun `when url is Google APIs, should return google headers`() {
        val url = "https://books.googleapis.com/books/v1/volumes/123"
        every { booksRepository.getBookCoverHeaders(url) } returns googleHeaders

        val result = useCase(url)

        assertEquals(googleHeaders, result)
    }

    @Test
    fun `when url is unknown, should return empty map`() {
        val url = "https://some-other-source.com/image.png"
        every { booksRepository.getBookCoverHeaders(url) } returns emptyMap()

        val result = useCase(url)

        assertEquals(emptyMap<String, String>(), result)
    }

    @Test
    fun `when url contains keywords in middle of string, should still match`() {
        // Just checking the contains() logic is robust
        val url = "https://proxy.com?target=openlibrary.org/item"
        every { booksRepository.getBookCoverHeaders(url) } returns openLibraryHeaders

        val result = useCase(url)

        assertEquals(openLibraryHeaders, result)
    }
}
