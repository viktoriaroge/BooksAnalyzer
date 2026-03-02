package com.viroge.booksanalyzer.ui.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R

@Composable
fun PvBookCoverAsyncImage(
    url: String?,
    requestHeaders: Map<String, String>,
    @DrawableRes defaultImageRes: Int = R.drawable.ic_empty_book_cover,
    size: PvBookCoverImageSize = PvBookCoverImageSize.SMALL,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier,
) = AsyncImage(
    modifier = modifier
        .size(
            width = when (size) {
                PvBookCoverImageSize.XSMALL -> 60.dp
                PvBookCoverImageSize.SMALL -> 80.dp
                PvBookCoverImageSize.MEDIUM -> 120.dp
                PvBookCoverImageSize.LARGE -> 160.dp
                PvBookCoverImageSize.XLARGE -> 200.dp
                PvBookCoverImageSize.XXLARGE -> 240.dp
            },
            height = when (size) {
                PvBookCoverImageSize.XSMALL -> 90.dp
                PvBookCoverImageSize.SMALL -> 120.dp
                PvBookCoverImageSize.MEDIUM -> 180.dp
                PvBookCoverImageSize.LARGE -> 240.dp
                PvBookCoverImageSize.XLARGE -> 300.dp
                PvBookCoverImageSize.XXLARGE -> 360.dp
            },
        )
        .clip(RoundedCornerShape(12.dp)),
    model = ImageRequest.Builder(context = LocalContext.current)
        .data(data = url)
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

enum class PvBookCoverImageSize {
    XSMALL,
    SMALL,
    MEDIUM,
    LARGE,
    XLARGE,
    XXLARGE,
}