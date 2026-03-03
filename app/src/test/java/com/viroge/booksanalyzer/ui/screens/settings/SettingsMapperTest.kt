package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.nav.Routes
import com.viroge.booksanalyzer.ui.screens.settings.SettingsItemType
import com.viroge.booksanalyzer.ui.screens.settings.SettingsMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsMapperTest {

    private val mapper = SettingsMapper()
    private val testVersion = "1.2.3-debug"

    @Test
    fun `map RECENTLY_DELETED should have correct title res and route`() {
        val types = listOf(SettingsItemType.RECENTLY_DELETED)

        val result = mapper.map(types, testVersion)
        val row = result.rowStates.first()

        assertEquals(R.string.recently_deleted_screen_name, row.titleRes)
        assertEquals(Routes.RECENTLY_DELETED_BOOKS, row.route)
        assertFalse(row.isHeader)
    }

    @Test
    fun `map VERSION should be disabled and contain the passed version string`() {
        val types = listOf(SettingsItemType.VERSION)

        val result = mapper.map(types, testVersion)
        val row = result.rowStates.first()

        assertEquals(R.string.settings_screen_item_version_title, row.titleRes)
        assertEquals(testVersion, row.subtitle) // Verifies dynamic version mapping
        assertFalse(row.isEnabled)
        assertNull(row.route)
    }

    @Test
    fun `map BOOKS_HEADER should be marked as header and have an icon`() {
        val types = listOf(SettingsItemType.BOOKS_HEADER)

        val result = mapper.map(types, testVersion)
        val row = result.rowStates.first()

        assertEquals(Icons.Default.LocalLibrary, row.icon)
        assertEquals(true, row.isHeader)
        assertEquals(R.string.settings_screen_books_section_title, row.titleRes)
    }

    @Test
    fun `map should preserve the order of input types`() {
        val types = listOf(
            SettingsItemType.BOOKS_HEADER,
            SettingsItemType.VERSION
        )

        val result = mapper.map(types, testVersion)

        assertEquals(2, result.rowStates.size)
        assertEquals(R.string.settings_screen_books_section_title, result.rowStates[0].titleRes)
        assertEquals(R.string.settings_screen_item_version_title, result.rowStates[1].titleRes)
    }
}
