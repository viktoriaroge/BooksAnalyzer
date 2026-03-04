package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.BooksRepository
import com.viroge.booksanalyzer.data.local.books.InsertBookResult
import com.viroge.booksanalyzer.domain.model.Book
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveBookUseCaseTest {
    private val repo = mockk<BooksRepository>()
    private val useCase = SaveBookUseCase(repo)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        val book = mockk<Book>()
        coEvery { repo.insertFromBook(book) } returns InsertBookResult(bookId = "1", wasInserted = true)

        val result = useCase(book)

        assert(result.isSuccess)
        assertEquals("1", result.getOrNull()?.bookId)
    }
}
