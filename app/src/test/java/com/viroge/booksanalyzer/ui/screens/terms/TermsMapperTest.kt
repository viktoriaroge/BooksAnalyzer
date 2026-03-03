package com.viroge.booksanalyzer.ui.screens.terms

import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.screens.terms.TermsItemType
import com.viroge.booksanalyzer.ui.screens.terms.TermsMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class TermsMapperTest {

    private val mapper = TermsMapper()

    @Test
    fun `map PAGEVOW should return correct resources`() {
        val types = listOf(TermsItemType.PAGEVOW)

        val result = mapper.map(types)
        val row = result.rowStates.first()

        assertEquals(R.string.terms_screen_pagevow_title, row.titleRes)
        assertEquals(R.string.terms_screen_pagevow_desc, row.subtitleRes)
    }

    @Test
    fun `map SCRIPTORIUM should return correct resources`() {
        val types = listOf(TermsItemType.SETTINGS)

        val result = mapper.map(types)
        val row = result.rowStates.first()

        assertEquals(R.string.terms_screen_scriptorium_title, row.titleRes)
        assertEquals(R.string.terms_screen_scriptorium_desc, row.subtitleRes)
    }

    @Test
    fun `map should return all requested items in the correct order`() {
        val types = listOf(TermsItemType.SOURCE, TermsItemType.DELETE_BOOK)

        val result = mapper.map(types)

        assertEquals(2, result.rowStates.size)
        assertEquals(R.string.terms_screen_origin_title, result.rowStates[0].titleRes)
        assertEquals(R.string.terms_screen_banish_title, result.rowStates[1].titleRes)
    }

    @Test
    fun `all mapped items should have icons and titles enabled`() {
        val types = TermsItemType.entries // Test every single enum entry

        val result = mapper.map(types)

        result.rowStates.forEach { row ->
            assertEquals(true, row.showTitle)
            assertEquals(true, row.showSubtitle)
        }
    }
}
