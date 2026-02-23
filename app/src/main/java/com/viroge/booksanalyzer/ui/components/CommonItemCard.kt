package com.viroge.booksanalyzer.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun CommonItemCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) = Card(
    modifier = modifier,
    shape = shape,
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    elevation = elevation,
    onClick = onClick,
    content = content,
)