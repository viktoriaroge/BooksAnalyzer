package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvSkeletonArea
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookLoadingScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    screenState: ConfirmBookScreenState.Loading,
    onBack: () -> Unit,
) {
    val book = screenState.bookData ?: return
    val values = screenState.screenValues

    val appScaffoldPadding = LocalAppScaffoldPadding.current

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenTitle),
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = appScaffoldPadding.calculateBottomPadding()),
        ) {

            PvBookCoverHeader(
                imageUrl = book.coverUrl,
                // Animation parameters:
                animate = true,
                animationKey = book.animationKey,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            if (screenState.isManual) {
                ManualSkeletonLoader()
            } else {
                DefaultSkeletonLoader()
            }
        }
    }
}


@Composable
private fun DefaultSkeletonLoader() {
    PvSkeletonArea(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        // Cover Button
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            cornerRadius = 16.dp,
        )

        // Title
        Spacer(Modifier.height(24.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
        )

        // Authors
        Spacer(Modifier.height(12.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
        )

        // Meta
        Spacer(Modifier.height(12.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
        )

        // Source Badge
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Item(
                modifier = Modifier
                    .width(40.dp)
                    .height(16.dp)
            )
            Item(
                modifier = Modifier
                    .width(80.dp)
                    .height(24.dp),
                cornerRadius = 12.dp
            )
        }

        // Save button
        Spacer(Modifier.height(18.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            cornerRadius = 16.dp,
        )
    }
}

@Composable
private fun ManualSkeletonLoader() {
    PvSkeletonArea(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        // Cover Button
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            cornerRadius = 16.dp,
        )

        // Title
        Spacer(Modifier.height(26.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
        Spacer(Modifier.height(18.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )

        // Authors
        Spacer(Modifier.height(38.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )

        // Year
        Spacer(Modifier.height(38.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )
}
}
