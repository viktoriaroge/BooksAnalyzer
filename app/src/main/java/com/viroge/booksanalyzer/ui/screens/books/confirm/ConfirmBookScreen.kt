package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvButtonType
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: ConfirmBookUiState,
    onOpenCoverPicker: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {

    if (state.screenState.isInManualMode) return
    val book = state.bookData ?: return

    val appScaffoldPadding = LocalAppScaffoldPadding.current
    val values = state.screenState.screenValues

    val scrollState = rememberScrollState()
    val scrollFraction = remember { derivedStateOf { (scrollState.value / 100f).coerceIn(0f, 1f) } }.value
    val appBarColor = lerp(
        start = Color.Transparent,
        stop = MaterialTheme.colorScheme.surface,
        fraction = scrollFraction
    )

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenTitleConfirm),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(bottom = appScaffoldPadding.calculateBottomPadding()),
        ) {

            PvBookCoverHeader(
                imageUrl = book.url,
                headersForBookCover = book.headers,
                // Animation parameters:
                animate = true,
                animationKey = book.animationKey,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            PvButton(
                buttonType = PvButtonType.Secondary,
                text = stringResource(values.changeCoverButtonLabel),
                onClick = onOpenCoverPicker,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (book.authors.isNotBlank()) {
                Text(
                    text = book.authors,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            book.isbn13?.let { isbn ->
                Text(
                    text = stringResource(values.isbnLabel, isbn),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(values.sourceLabel),
                    style = MaterialTheme.typography.bodySmall,
                )
                PvBookSourceBadge(sourceText = book.source.label.asString())
            }

            PvButton(
                text = stringResource(values.saveButtonLabel),
                onClick = onSave,
                enabled = !state.screenState.isSaving,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
