package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.viroge.booksanalyzer.R
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
}
