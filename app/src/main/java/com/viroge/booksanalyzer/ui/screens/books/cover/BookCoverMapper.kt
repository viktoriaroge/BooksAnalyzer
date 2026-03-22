package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.provider.BookCoverCandidate
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

    fun map(entry: BookCoverCandidate): BookCoverPickerItem = BookCoverPickerItem(
        url = entry.url,
    )
}
