package com.viroge.booksanalyzer.ui.screens.bookcover

import com.viroge.booksanalyzer.domain.model.Book
import com.viroge.booksanalyzer.domain.model.BookSource
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.domain.usecase.BookCoverCandidate
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverCandidatesUseCase
import com.viroge.booksanalyzer.domain.usecase.GetBookCoverHeadersUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoverPickerViewModelTest {

    private val getCoverCandidates = mockk<GetBookCoverCandidatesUseCase>()
    private val getCoverHeaders = mockk<GetBookCoverHeadersUseCase>()
    private val mapper = BookCoverMapper() // Using real mapper since it's pure logic

    private lateinit var viewModel: CoverPickerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getCoverHeaders(any()) } returns emptyMap()
        every { getCoverCandidates(any()) } returns emptyList()

        viewModel = CoverPickerViewModel(getCoverCandidates, getCoverHeaders, mapper)
    }

    @Test
    fun `openCoverPicker should fetch candidates and update state on first call`() = runTest {
        val book = Book(
            id = "1234",
            coverUrl = "old_url",
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
        val candidates = listOf(
            BookCoverCandidate("", emptyMap()),
            BookCoverCandidate("new_url", mapOf("Header" to "Val"))
        )
        every { getCoverCandidates(book) } returns candidates

        viewModel.openCoverPicker(book)

        val state = viewModel.state.value
        assertTrue(state.initialized)
        assertTrue(state.isOpen)
        assertEquals(2, state.bookCovers.size)
        assertEquals("old_url", state.selectedCover.url)
        assertFalse(state.isLoading)
    }

    @Test
    fun `addManualUrl should add new candidate to the top and clear input`() = runTest {
        val manualUrl = "https://my-manual-cover.com/img.jpg"
        val manualHeaders = mapOf("Auth" to "Token")
        every { getCoverHeaders(manualUrl) } returns manualHeaders

        viewModel.onManualUrlChange(manualUrl)

        viewModel.addManualUrl()

        val state = viewModel.state.value
        assertEquals(manualUrl, state.bookCovers.first().url)
        assertEquals(manualHeaders, state.bookCovers.first().headers)
        assertEquals("", state.manualUrlInput)
    }

    @Test
    fun `addManualUrl should not add if url is already in list`() = runTest {
        val existingUrl = "https://existing.com"
        // Seed the state with an existing cover
        viewModel.openCoverPicker(
            Book(
                id = "1234",
                coverUrl = existingUrl,
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
        )
        every { getCoverCandidates(any()) } returns listOf(BookCoverCandidate(existingUrl, emptyMap()))

        viewModel.onManualUrlChange(existingUrl)

        viewModel.addManualUrl()

        // We expect only 2 covers (the one we seeded + the default empty one from UseCase)
        // If duplicates weren't handled, there would be 3.
        assertEquals(1, viewModel.state.value.bookCovers.filter { it.url == existingUrl }.size)
    }

    @Test
    fun `selectCover should update selectedCover and close picker`() {
        val newUrl = "https://selected.com"
        val headers = mapOf("X-Source" to "Selection")
        every { getCoverHeaders(newUrl) } returns headers

        viewModel.selectCover(newUrl)

        val state = viewModel.state.value
        assertEquals(newUrl, state.selectedCover.url)
        assertEquals(headers, state.selectedCover.headers)
        assertFalse(state.isOpen)
    }
}
