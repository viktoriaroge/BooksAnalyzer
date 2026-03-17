package com.viroge.booksanalyzer.ui.components.bookcover

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class PvBookCoverImageSize(
    val width: Dp,
    val height: Dp,
) {
    object XSmall : PvBookCoverImageSize(width = 60.dp, height = 90.dp)
    object Small : PvBookCoverImageSize(width = 80.dp, height = 120.dp)
    object Medium : PvBookCoverImageSize(width = 120.dp, height = 180.dp)
    object Large : PvBookCoverImageSize(width = 160.dp, height = 240.dp)
    object XLarge : PvBookCoverImageSize(width = 200.dp, height = 300.dp)
    object XXLarge : PvBookCoverImageSize(width = 240.dp, height = 360.dp)
}
