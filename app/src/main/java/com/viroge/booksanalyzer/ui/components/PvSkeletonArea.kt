package com.viroge.booksanalyzer.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PvSkeletonArea(
    modifier: Modifier = Modifier,
    content: @Composable SkeletonScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "skeleton_heartbeat")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    val scope = remember { SkeletonScope() }
    scope.updateAlpha(alpha)

    Column(modifier = modifier) {
        scope.content()
    }
}

class SkeletonScope {

    private var _alpha = mutableStateOf(0.2f)
    val alpha: Float get() = _alpha.value

    fun updateAlpha(newAlpha: Float) {
        _alpha.value = newAlpha
    }

    @Composable
    fun Item(
        modifier: Modifier,
        cornerRadius: Dp = 4.dp,
        color: Color = MaterialTheme.colorScheme.onSurface,
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .drawBehind {
                    drawRect(color = color.copy(alpha = alpha))
                }
        )
    }
}
