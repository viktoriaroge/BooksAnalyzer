package com.viroge.booksanalyzer.domain.usecase

import com.viroge.booksanalyzer.data.repository.BooksRepository
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.usecase.book.GetRecentlyDeletedBooksUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GetRecentlyDeletedBooksUseCaseTest {

    private val repo = mockk<BooksRepository>()

    @OptIn(ExperimentalTime::class)
    @Test
    fun `should only return books deleted within the last 7 days`() = runTest {
        val fixedNow = Instant.parse("2026-03-02T12:00:00Z")
        val testClock = object : Clock {
            override fun now(): Instant = fixedNow
        }

        val validBook = Book(
            id = "valid", // just for testing purposes
            lastMarkedToDelete = (fixedNow - 6.days).toEpochMilliseconds(),
            toBeDeleted = false,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
        )
        val expiredBook = Book(
            id = "expired", // just for testing purposes
            lastMarkedToDelete = (fixedNow - 8.days).toEpochMilliseconds(),
            toBeDeleted = true,
            source = BookSource.MANUAL,
            sourceId = null,
            status = ReadingStatus.NOT_STARTED,
            title = "title",
            authors = emptyList(),
            coverRequestHeaders = emptyMap(),
            createdAtEpochMs = 0,
            lastOpenAtEpochMs = 0,
        )
        every { repo.observePendingDeleteBooks() } returns flowOf(listOf(validBook, expiredBook))

        val useCase = GetRecentlyDeletedBooksUseCase(repo, testClock)
        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("valid", result.first().id)
    }
}