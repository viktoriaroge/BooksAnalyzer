package com.viroge.booksanalyzer.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PvLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float = { 0f },
) = LinearProgressIndicator(
    progress = progress,
    modifier = modifier.fillMaxWidth(),
    trackColor = MaterialTheme.colorScheme.primary,
)

@Composable
fun PvLinearProgressIndicator(
    modifier: Modifier = Modifier,
) = LinearProgressIndicator(
    modifier = modifier.fillMaxWidth(),
    trackColor = MaterialTheme.colorScheme.primary,
)
