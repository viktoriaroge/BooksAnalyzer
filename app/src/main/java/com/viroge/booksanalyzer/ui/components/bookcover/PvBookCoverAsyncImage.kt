package com.viroge.booksanalyzer.ui.components.bookcover

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.viroge.booksanalyzer.BooksAnalyzerApp
import com.viroge.booksanalyzer.R

@Composable
fun PvBookCoverAsyncImage(
    modifier: Modifier = Modifier,
    url: String?,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
    imageSize: PvBookCoverImageSize = PvBookCoverImageSize.Small,
    contentScale: ContentScale = ContentScale.Crop,
    // Animation parameters:
    animate: Boolean = false,
    animationKey: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    Box(
        modifier = modifier
            .size(
                width = imageSize.width,
                height = imageSize.height,
            )
            .clip(RoundedCornerShape(12.dp))
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        if (animate && animationKey != null && sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                PvAsyncImage(
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(
                                key = animationKey
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    url = url,
                    defaultImageRes = defaultImageRes,
                    contentScale = contentScale,
                )
            }
        } else {
            PvAsyncImage(
                url = url,
                defaultImageRes = defaultImageRes,
                contentScale = contentScale,
            )
        }
    }
}

@Composable
private fun PvAsyncImage(
    modifier: Modifier = Modifier,
    url: String?,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val myImageLoader = (context.applicationContext as BooksAnalyzerApp).imageLoader

    AsyncImage(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        model = url,
        imageLoader = myImageLoader,
        error = painterResource(id = defaultImageRes),
        contentScale = contentScale,
        contentDescription = null,
    )
}
