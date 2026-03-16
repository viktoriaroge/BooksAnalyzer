package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LibraryFloatingActionButton(
    isFullLibrary: Boolean,
    fabShowFullText: String,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .padding(16.dp)
                .sharedElement(
                    rememberSharedContentState(key = "library_fab"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    renderInOverlayDuringTransition = true,
                    boundsTransform = { _, _ -> tween(durationMillis = 500) },
                )
                .skipToLookaheadSize()
        ) {
            AnimatedContent(
                targetState = isFullLibrary,
                label = "FAB_Content_Animation",
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                }
            ) { isFull ->
                if (isFull) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                } else {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(fabShowFullText)
                    }
                }
            }
        }
    }
}
