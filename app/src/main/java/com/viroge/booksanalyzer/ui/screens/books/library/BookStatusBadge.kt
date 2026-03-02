package com.viroge.booksanalyzer.ui.screens.books.library

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
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.ui.screens.books.StatusMapper

@Composable
fun BookStatusBadge(
    status: ReadingStatus,
    modifier: Modifier = Modifier,
) {
    val statusModel = StatusMapper.getUiModel(status)

    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 6.dp))
            .background(statusModel.color)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = statusModel.text,
            color = MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
