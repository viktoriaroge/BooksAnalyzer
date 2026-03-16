package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.usecase.book.RestoreBookUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RestoreBookUseCaseTest {

    private val repo = mockk<BooksRepository>()
    private val useCase = RestoreBookUseCase(repo)

    @Test
    fun `invoke should return success when repository successfully restores book`() = runTest {
        val bookId = "test-book-id"
        coEvery { repo.restoreBookMarkedToDelete(bookId) } returns Unit

        val result = useCase(bookId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repo.restoreBookMarkedToDelete(bookId) }
    }

    @Test
    fun `invoke should return failure when repository throws an exception`() = runTest {
        val bookId = "test-book-id"
        val exception = RuntimeException("Database error")
        coEvery { repo.restoreBookMarkedToDelete(bookId) } throws exception

        val result = useCase(bookId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() == exception)
        coVerify(exactly = 1) { repo.restoreBookMarkedToDelete(bookId) }
    }
}