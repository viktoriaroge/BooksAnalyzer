package com.viroge.booksanalyzer.ui.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CommonItemCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) = Card(
    modifier = modifier,
    shape = shape,
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    onClick = onClick,
    content = content,
)