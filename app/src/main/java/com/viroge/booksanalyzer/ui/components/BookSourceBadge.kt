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
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.BookSource

@Composable
fun BookSourceBadge(
    source: BookSource,
    modifier: Modifier = Modifier,
    showFullSourceName: Boolean = false,
) {
    val defaultColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val (text, color) = when (source) {
        BookSource.GOOGLE_BOOKS -> (if (showFullSourceName) "Google Books" else "G") to defaultColor
        BookSource.OPEN_LIBRARY -> (if (showFullSourceName) "Open Library" else "O") to defaultColor
        BookSource.MANUAL -> (if (showFullSourceName) "Added Manually" else "M") to defaultColor
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
