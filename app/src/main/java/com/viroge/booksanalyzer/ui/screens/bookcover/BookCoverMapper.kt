package com.viroge.booksanalyzer.ui.screens.bookcover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.usecase.BookCoverCandidate
import javax.inject.Inject

class BookCoverMapper @Inject constructor() {

    fun getStaticScreenValues(): BookCoverPickerScreenValues = BookCoverPickerScreenValues(
        screenTitle = R.string.book_cover_picker_name,
        inputFieldLabel = R.string.book_cover_picker_input_field_label,
        inputFieldIcon = Icons.Default.Check,
    )

    fun map(
        candidate: BookCoverCandidate,
    ): BookCoverState = BookCoverState(
        url = candidate.url,
        headers = candidate.headers,
    )
}
