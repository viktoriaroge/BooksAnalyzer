package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.viroge.booksanalyzer.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookCoverMapper @Inject constructor() {

    fun getStaticScreenValues(): BookCoverPickerScreenValues = BookCoverPickerScreenValues(
        screenTitle = R.string.book_cover_picker_name,
    )

    fun getContentStateValues(): BookCoverPickerContentValues = BookCoverPickerContentValues(
        inputFieldLabel = R.string.book_cover_picker_input_field_label,
        inputFieldIcon = Icons.Default.Check,
    )

    fun map(entry: BookCover): BookCoverPickerItem = BookCoverPickerItem(
        shouldReportOnFailToLoad = entry.url.trim().isNotEmpty(),
        url = entry.url,
    )

    fun map(url: String): BookCoverPickerItem = BookCoverPickerItem(
        shouldReportOnFailToLoad = url.trim().isNotEmpty(),
        url = url,
    )
}
