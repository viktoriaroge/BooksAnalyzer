package com.viroge.booksanalyzer.ui.common

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.viroge.booksanalyzer.BuildConfig
import com.viroge.booksanalyzer.R

@Composable
fun CommonAsyncImage(
    url: String?,
    @DrawableRes defaultImageRes: Int = R.drawable.default_book,
    size: CommonAsyncImageSize = CommonAsyncImageSize.SMALL,
    modifier: Modifier = Modifier,
) {
    // NOTE: Adding a user email in the header helps us get more allowed requests per second.
    // For Open Library API that raises our requests from 1 to 3 per second.
    val userEmail = BuildConfig.USER_EMAIL // TODO: later pass from ViewModel down possibly (need to get it from DI)

    return AsyncImage(
        modifier = modifier
            .size(
                width = when (size) {
                    CommonAsyncImageSize.XSMALL -> 60.dp
                    CommonAsyncImageSize.SMALL -> 80.dp
                    CommonAsyncImageSize.MEDIUM -> 120.dp
                    CommonAsyncImageSize.LARGE -> 160.dp
                    CommonAsyncImageSize.XLARGE -> 200.dp
                    CommonAsyncImageSize.XXLARGE -> 240.dp
                },
                height = when (size) {
                    CommonAsyncImageSize.XSMALL -> 90.dp
                    CommonAsyncImageSize.SMALL -> 120.dp
                    CommonAsyncImageSize.MEDIUM -> 180.dp
                    CommonAsyncImageSize.LARGE -> 240.dp
                    CommonAsyncImageSize.XLARGE -> 300.dp
                    CommonAsyncImageSize.XXLARGE -> 360.dp
                },
            ),
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(data = url)
            .addHeader(name = "User-Agent", value = "BooksAnalyzerApp ($userEmail)")
            .crossfade(enable = true)
            .listener(
                onCancel = { Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Canceled for: $url") },
                onError = { _, _ -> Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Failed for: $url") },
                onSuccess = { _, _ -> Log.println(Log.DEBUG, "CommonAsyncImage", "---> Loading Succeeded for: $url") },
            )
            .build(),
        error = painterResource(id = defaultImageRes),
        contentDescription = null,
    )
}

enum class CommonAsyncImageSize {
    XSMALL,
    SMALL,
    MEDIUM,
    LARGE,
    XLARGE,
    XXLARGE,
}