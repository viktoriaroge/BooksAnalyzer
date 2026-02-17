package com.viroge.booksanalyzer.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.R

@Composable
fun CommonAsyncImage(
    url: String?,
    @DrawableRes defaultImageRes: Int = R.drawable.default_book,
    size: CommonAsyncImageSize = CommonAsyncImageSize.SMALL,
    modifier: Modifier = Modifier,
) = AsyncImage(
    modifier = modifier
        .size(
            width = when (size) {
                CommonAsyncImageSize.XSMALL -> 60.dp
                CommonAsyncImageSize.SMALL -> 80.dp
                CommonAsyncImageSize.MEDIUM -> 120.dp
                CommonAsyncImageSize.LARGE -> 160.dp
            },
            height = when (size) {
                CommonAsyncImageSize.XSMALL -> 90.dp
                CommonAsyncImageSize.SMALL -> 120.dp
                CommonAsyncImageSize.MEDIUM -> 180.dp
                CommonAsyncImageSize.LARGE -> 240.dp
            },
        ),
    model = ImageRequest.Builder(context = LocalContext.current)
        .data(data = url)
        .crossfade(enable = true)
        .build(),
    error = painterResource(id = defaultImageRes),
    contentDescription = null,
)

enum class CommonAsyncImageSize {
    XSMALL,
    SMALL,
    MEDIUM,
    LARGE,
}