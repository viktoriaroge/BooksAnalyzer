package com.viroge.booksanalyzer.ui.components.bookcover

import android.util.Log
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
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R

@Composable
fun PvBookCoverAsyncImage(
    modifier: Modifier = Modifier,
    url: String?,
    requestHeaders: Map<String, String>,
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
                    requestHeaders = requestHeaders,
                    defaultImageRes = defaultImageRes,
                    contentScale = contentScale,
                )
            }
        } else {
            PvAsyncImage(
                url = url,
                requestHeaders = requestHeaders,
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
    requestHeaders: Map<String, String>,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = url)
            .memoryCacheKey(url)
            .placeholderMemoryCacheKey(url)
            .let { chain ->
                for ((name, value) in requestHeaders) {
                    chain.addHeader(name, value)
                }
                chain
            }
            .crossfade(enable = true)
            .listener(
                onCancel = { Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Canceled for: $url") },
                onError = { _, _ -> Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Failed for: $url") },
                onSuccess = { _, _ -> Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Succeeded for: $url") },
            )
            .build(),
        error = painterResource(id = defaultImageRes),
        contentScale = contentScale,
        contentDescription = null,
    )
}
