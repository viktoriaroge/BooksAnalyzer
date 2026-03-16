package com.viroge.booksanalyzer.ui.screens.books.deleted

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ViewModelTest
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.usecase.book.GetRecentlyDeletedBooksUseCase
import com.viroge.booksanalyzer.domain.usecase.book.RestoreBookUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class RecentlyDeletedViewModelTest : ViewModelTest() {

    private val getRecentlyDeletedBooks = mockk<GetRecentlyDeletedBooksUseCase>()
    private val restoreBookUseCase = mockk<RestoreBookUseCase>()
    private val mapper = RecentlyDeletedBookMapper()

    private lateinit var viewModel: RecentlyDeletedViewModel

    @Before
    fun setup() {
        // Default behavior: return an empty flow of books
        every { getRecentlyDeletedBooks() } returns flowOf(emptyList())

        viewModel = RecentlyDeletedViewModel(
            getRecentlyDeletedBooks,
            restoreBookUseCase,
            mapper
        )
    }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `state should emit mapped books when use case provides data`() = runTest {
        val fixedNow = Instant.parse("2026-03-02T12:00:00Z")
        val testBooks = listOf(
            Book(
                id = "123",
                title = "Test Book",
                authors = listOf("Author"),
                publishedYear = null,
                isbn13 = null,
                source = BookSource.GOOGLE_BOOKS,
                coverUrl = "existing_url",
                sourceId = null,
                status = ReadingStatus.NOT_STARTED,
                coverRequestHeaders = emptyMap(),
                createdAtEpochMs = 0,
                lastOpenAtEpochMs = 0,
                lastMarkedToDelete = (fixedNow - 6.days).toEpochMilliseconds(),
                toBeDeleted = true,
            )
        )
        every { getRecentlyDeletedBooks() } returns flowOf(testBooks)

        // We re-init to trigger the flow collection in the new state
        viewModel = RecentlyDeletedViewModel(getRecentlyDeletedBooks, restoreBookUseCase, mapper)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }

        val state = viewModel.state.value
        assertEquals(1, state.books.size)
        assertEquals("Test Book", state.books.first().title)
        assertEquals(R.string.recently_deleted_screen_name, state.screenValues.screenName)
    }

    @Test
    fun `restoreBook should call use case with correct ID`() = runTest {
        val bookId = "999"
        coEvery { restoreBookUseCase(bookId) } returns Result.success(Unit)

        viewModel.restoreBook(bookId)

        io.mockk.coVerify { restoreBookUseCase(bookId) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state should catch errors and emit message`() = runTest {
        every { getRecentlyDeletedBooks() } returns flow { throw Exception("DB Error") }

        viewModel = RecentlyDeletedViewModel(getRecentlyDeletedBooks, restoreBookUseCase, mapper)

        val messages = mutableListOf<String?>()
        // 1. Start collecting the message flow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.message.collect { messages.add(it) }
        }

        // 2. Start collecting the state flow to trigger the catch block
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }

        assertEquals("Failed to retrieve Books pending deletion.", messages.first())
    }
}
