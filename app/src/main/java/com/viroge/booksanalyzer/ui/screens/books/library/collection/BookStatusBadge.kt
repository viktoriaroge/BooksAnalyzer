package com.viroge.booksanalyzer.ui.screens.books.library.collection

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BookStatusBadge(
    modifier: Modifier = Modifier,
    @StringRes statusTextRes: Int,
    statusColor: Color,
) {
    BookStatusBadge(
        modifier = modifier,
        statusText = stringResource(statusTextRes),
        statusColor = statusColor,
    )
}

@Composable
fun BookStatusBadge(
    modifier: Modifier = Modifier,
    statusText: String,
    statusColor: Color,
) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 6.dp))
            .background(statusColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = statusText,
            color = MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
