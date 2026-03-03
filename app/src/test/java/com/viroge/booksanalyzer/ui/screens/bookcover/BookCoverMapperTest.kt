package com.viroge.booksanalyzer.ui.screens.bookcover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.usecase.BookCoverCandidate
import org.junit.Assert.assertEquals
import org.junit.Test

class BookCoverMapperTest {

    private val mapper = BookCoverMapper()

    @Test
    fun `getStaticScreenValues should return correct resource IDs and icons`() {
        val result = mapper.getStaticScreenValues()

        assertEquals(R.string.book_cover_picker_name, result.screenTitle)
        assertEquals(R.string.book_cover_picker_input_field_label, result.inputFieldLabel)
        assertEquals(Icons.Default.Check, result.inputFieldIcon)
    }

    @Test
    fun `map should correctly transform domain candidate to UI state`() {
        val url = "https://example.com/cover.jpg"
        val headers = mapOf("Authorization" to "Bearer 123")
        val candidate = BookCoverCandidate(url = url, headers = headers)

        val result = mapper.map(candidate)

        assertEquals(url, result.url)
        assertEquals(headers, result.headers)
    }

    @Test
    fun `map should handle empty candidates correctly`() {
        val candidate = BookCoverCandidate(url = "", headers = emptyMap())

        val result = mapper.map(candidate)

        assertEquals("", result.url)
        assertEquals(0, result.headers.size)
    }
}
