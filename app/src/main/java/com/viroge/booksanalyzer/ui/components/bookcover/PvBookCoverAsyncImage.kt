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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.BooksAnalyzerApp
import com.viroge.booksanalyzer.R

@Composable
fun PvBookCoverAsyncImage(
    modifier: Modifier = Modifier,
    url: String?,
    imageSize: PvBookCoverImageSize = PvBookCoverImageSize.Small,
    contentScale: ContentScale = ContentScale.Crop,

    // Animation parameters:
    animate: Boolean = false,
    animationKey: String? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,

    // Loading error:
    reportOnLoadingError: Boolean = false,
    onLoadingError: (() -> Unit)? = null,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
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
                    contentScale = contentScale,

                    // Loading error:
                    reportOnLoadingError = reportOnLoadingError,
                    onLoadingError = onLoadingError,
                    defaultImageRes = defaultImageRes,
                )
            }
        } else {
            PvAsyncImage(
                url = url,
                contentScale = contentScale,

                // Loading error:
                reportOnLoadingError = reportOnLoadingError,
                onLoadingError = onLoadingError,
                defaultImageRes = defaultImageRes,
            )
        }
    }
}

@Composable
private fun PvAsyncImage(
    modifier: Modifier = Modifier,
    url: String?,
    contentScale: ContentScale = ContentScale.Crop,

    reportOnLoadingError: Boolean = false,
    onLoadingError: (() -> Unit)? = null,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
) {
    val context = LocalContext.current
    val imageLoader = (context.applicationContext as BooksAnalyzerApp).imageLoader

    if (reportOnLoadingError) {
        // Build the request with a listener to catch the error in Coil 2.x:
        val request = remember(url) {
            ImageRequest.Builder(context)
                .data(url)
                .listener(
                    onError = { _, _ ->
                        onLoadingError?.invoke()
                    }
                )
                .build()
        }

        AsyncImage(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            model = request, // Pass the request object instead of the string
            imageLoader = imageLoader,
            contentScale = contentScale,
            contentDescription = null,
            // Show placeholder only if we aren't "pruning"
            error = if (onLoadingError == null) painterResource(id = defaultImageRes) else null,
        )

    } else {
        AsyncImage(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            model = url,
            imageLoader = imageLoader,
            error = painterResource(id = defaultImageRes),
            contentScale = contentScale,
            contentDescription = null,
        )
    }
}
