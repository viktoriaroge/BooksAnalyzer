package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.domain.BookMapper
import com.viroge.booksanalyzer.domain.model.Book
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ValidateAndGetManualBookUseCaseTest {
    private val mapper = mockk<BookMapper>()
    private val useCase = ValidateAndGetManualBookUseCase(mapper)

    @Test
    fun `invoke should return failure when title is blank`() {
        val result = useCase(title = "   ", authors = "", null, null, null)

        assert(result.isFailure)
        assertEquals("Title is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should return success and map when title is valid`() {
        val expectedBook = mockk<Book>()
        every { mapper.mapFromManualInput(any(), any(), any(), any(), any()) } returns expectedBook

        val result = useCase(title = "Clean Code", authors = "Uncle Bob", null, null, null)

        assert(result.isSuccess)
        assertEquals(expectedBook, result.getOrNull())
    }
}
