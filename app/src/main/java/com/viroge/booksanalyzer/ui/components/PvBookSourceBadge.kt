package com.viroge.booksanalyzer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.BookSource

@Composable
fun PvBookSourceBadge(
    source: BookSource,
    modifier: Modifier = Modifier,
    showFullSourceName: Boolean = false,
) {
    val defaultColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val (text, color) = when (source) {
        BookSource.GOOGLE_BOOKS ->
            (if (showFullSourceName) stringResource(R.string.book_source_full_google_books)
            else stringResource(R.string.book_source_short_google_books)) to defaultColor

        BookSource.OPEN_LIBRARY ->
            (if (showFullSourceName) stringResource(R.string.book_source_full_open_library)
            else stringResource(R.string.book_source_short_open_library)) to defaultColor

        BookSource.MANUAL ->
            (if (showFullSourceName) stringResource(R.string.book_source_full_added_manually)
            else stringResource(R.string.book_source_short_added_manually)) to defaultColor
    }

    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 6.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
