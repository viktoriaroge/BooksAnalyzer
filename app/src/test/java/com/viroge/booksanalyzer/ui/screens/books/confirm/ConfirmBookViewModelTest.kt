package com.viroge.booksanalyzer.ui.screens.books.confirm

import com.viroge.booksanalyzer.ViewModelTest
import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.usecase.SaveBookResult
import com.viroge.booksanalyzer.domain.usecase.SaveBookUseCase
import com.viroge.booksanalyzer.domain.usecase.ValidateAndGetManualBookUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ConfirmBookViewModelTest : ViewModelTest() {

    private val saveBookUseCase = mockk<SaveBookUseCase>()
    private val validateManualBook = mockk<ValidateAndGetManualBookUseCase>()
    private val mapper = ConfirmBookMapper()
    private lateinit var viewModel: ConfirmBookViewModel

    @Before
    fun setup() {
        viewModel = ConfirmBookViewModel(saveBookUseCase, validateManualBook, mapper)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveBook should emit Saved event on success`() = runTest {
        val book = Book(
            id = "Test",
            title = "Clean Code",
            authors = emptyList(),
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
        coEvery { saveBookUseCase(any()) } returns Result.success(SaveBookResult("123"))

        val events = mutableListOf<ConfirmEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.saveBook(book, null, null)

        assert(events.first() is ConfirmEvent.Saved)
        assertEquals("123", (events.first() as ConfirmEvent.Saved).bookId)
    }

    @Test
    fun `saveManualBook should update state with error when validation fails`() = runTest {
        val errorMsg = "Title is required"
        every { validateManualBook(any(), any(), any(), any()) } returns Result.failure(Exception(errorMsg))

        viewModel.saveManualBook(null, null)

        assertEquals(errorMsg, viewModel.state.value.error)
    }
}
