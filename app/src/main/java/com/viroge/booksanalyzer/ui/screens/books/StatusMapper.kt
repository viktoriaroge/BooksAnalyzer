package com.viroge.booksanalyzer.ui.screens.books

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.ReadingStatus

object StatusMapper {

    @Composable
    fun getUiModel(status: ReadingStatus): ReadingStatusUiModel {
        val (text, color) = when (status) {
            ReadingStatus.NOT_STARTED -> stringResource(R.string.reading_status_not_started) to MaterialTheme.colorScheme.surfaceContainerHighest
            ReadingStatus.READING -> stringResource(R.string.reading_status_reading) to MaterialTheme.colorScheme.primaryContainer
            ReadingStatus.FINISHED -> stringResource(R.string.reading_status_finished) to MaterialTheme.colorScheme.tertiaryContainer
            ReadingStatus.ABANDONED -> stringResource(R.string.reading_status_abandoned) to MaterialTheme.colorScheme.errorContainer
        }
        return ReadingStatusUiModel(status, text, color)
    }
}

data class ReadingStatusUiModel(
    val status: ReadingStatus,
    val text: String,
    val color: Color,
)
