package com.viroge.booksanalyzer.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.ReadingStatus

@Composable
fun BookStatusBadge(
    status: ReadingStatus,
    modifier: Modifier = Modifier,
) {
    val (text, color) = when (status) {
        ReadingStatus.NOT_STARTED -> "Awaiting" to MaterialTheme.colorScheme.surfaceContainerHighest
        ReadingStatus.READING -> "Absorbing" to MaterialTheme.colorScheme.primaryContainer
        ReadingStatus.FINISHED -> "Conquered" to MaterialTheme.colorScheme.tertiaryContainer
        ReadingStatus.ABANDONED -> "DNF'd" to MaterialTheme.colorScheme.errorContainer
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
