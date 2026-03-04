package com.viroge.booksanalyzer.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PvBookCoverHeader(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    headersForBookCover: Map<String, String>,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds() // Prevents the blur from bleeding out
    ) {
        // Hazy background:
        val isDarkTheme = isSystemInDarkTheme()
        PvBookCoverAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 30.dp)
                .drawWithContent {
                    drawContent()
                    // Adjust slightly so the foreground pops
                    drawRect(
                        if (isDarkTheme) Color.Black.copy(alpha = 0.3f)
                        else Color.White.copy(alpha = 0.3f)
                    )
                },
            contentScale = ContentScale.Crop,
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = PvBookCoverImageSize.XXLARGE,
        )

        // Cover image:
        PvBookCoverAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 32.dp)
                .shadow(12.dp, RoundedCornerShape(12.dp)),
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = PvBookCoverImageSize.XXLARGE,
        )
    }
}