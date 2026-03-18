package com.viroge.booksanalyzer.ui.screens.books

import androidx.compose.runtime.Immutable
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.ui.common.util.UiText

@Immutable
sealed class BookReadingStatusUi(
    val domainStatus: ReadingStatus,
    val label: UiText,
) {
    object NotStarted : BookReadingStatusUi(
        domainStatus = ReadingStatus.NOT_STARTED,
        label = UiText.StringResource(R.string.reading_status_not_started),
    )

    object Reading : BookReadingStatusUi(
        domainStatus = ReadingStatus.READING,
        label = UiText.StringResource(R.string.reading_status_reading),
    )

    object Finished : BookReadingStatusUi(
        domainStatus = ReadingStatus.FINISHED,
        label = UiText.StringResource(R.string.reading_status_finished),
    )

    object Abandoned : BookReadingStatusUi(
        domainStatus = ReadingStatus.ABANDONED,
        label = UiText.StringResource(R.string.reading_status_abandoned),
    )

    companion object {
        fun allOptions(): List<BookReadingStatusUi> = listOf(
            NotStarted,
            Reading,
            Finished,
            Abandoned,
        )

        fun fromDomain(status: ReadingStatus) = when (status) {
            ReadingStatus.NOT_STARTED -> NotStarted
            ReadingStatus.READING -> Reading
            ReadingStatus.FINISHED -> Finished
            ReadingStatus.ABANDONED -> Abandoned
        }
    }
}
