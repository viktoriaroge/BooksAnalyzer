package com.viroge.booksanalyzer.ui.components.bookcover

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PvHazyBookCoverBackground(
    modifier: Modifier = Modifier,
    headerCoverSize: PvBookCoverImageSize,
    imageUrl: String?,
    alphaOverlayValue: Float = 0.4f,
) {
    val isDarkTheme = isSystemInDarkTheme()
    PvBookCoverAsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .height(headerCoverSize.height)
            .blur(radius = 25.dp)
            .drawWithContent {
                drawContent()
                // Adjust slightly so the foreground pops
                drawRect(
                    if (isDarkTheme) Color.Black.copy(alpha = alphaOverlayValue)
                    else Color.White.copy(alpha = alphaOverlayValue)
                )
            },
        contentScale = ContentScale.Crop,
        url = imageUrl,
        imageSize = headerCoverSize,
    )
}
