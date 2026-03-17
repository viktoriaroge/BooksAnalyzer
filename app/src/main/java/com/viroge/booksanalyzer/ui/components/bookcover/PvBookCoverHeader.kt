package com.viroge.booksanalyzer.ui.components.bookcover

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PvBookCoverHeader(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    headersForBookCover: Map<String, String>,
    // Animation parameters:
    animate: Boolean = false,
    animationKey: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val headerCoverSize = PvBookCoverImageSize.XXLarge
    val headerCoverPadding = 32.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerCoverSize.height + headerCoverPadding * 2)
            .clipToBounds() // Prevents the blur from bleeding out
    ) {
        // Hazy background:
        PvHazyBookCoverBackground(
            headerCoverSize = headerCoverSize,
            imageUrl = imageUrl,
            headersForBookCover = headersForBookCover,
        )

        // Cover image:
        PvBookCoverAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = headerCoverPadding),
            url = imageUrl,
            requestHeaders = headersForBookCover,
            imageSize = headerCoverSize,
            // Animation parameters:
            animate = animate,
            animationKey = animationKey,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
        )
    }
}
