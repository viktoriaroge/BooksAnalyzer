package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvButtonType
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@Composable
fun ConfirmManualBookScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: ConfirmBookUiState,
    onTitleChange: (String) -> Unit,
    onAuthorsChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onIsbnChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenCoverPicker: () -> Unit,
    onSave: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    if (!state.screenState.isInManualMode) return
    val book = state.bookData ?: return

    val screenValues = state.screenState.screenValues
    val editState = state.screenState.editState

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
                title = stringResource(screenValues.screenTitleManual),
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
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
                .imePadding(),
        ) {
            if (state.screenState.isSaving) {
                PvLinearProgressIndicator(modifier = Modifier.padding(top = 12.dp))
            }

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
                text = stringResource(screenValues.changeCoverButtonLabel),
                onClick = onOpenCoverPicker,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(screenValues.manualInstruction),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editTitle,
                onValueChange = { onTitleChange(it) },
                label = { Text(stringResource(screenValues.manualTitleLabel)) },
                singleLine = true,
                isError = editState.showTitleError,
                supportingText = {
                    if (editState.showTitleError) {
                        Text(
                            text = stringResource(screenValues.manualTitleError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editAuthors,
                onValueChange = { onAuthorsChange(it) },
                label = { Text(stringResource(screenValues.manualAuthorLabel)) },
                singleLine = true,
                isError = editState.showAuthorError,
                supportingText = {
                    if (editState.showAuthorError) {
                        Text(
                            text = stringResource(screenValues.manualAuthorError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editYear,
                onValueChange = { onYearChange(it) },
                label = { Text(stringResource(screenValues.manualYearLabel)) },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editIsbn13,
                onValueChange = { onIsbnChange(it) },
                label = { Text(stringResource(screenValues.manualIsbn13Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(16.dp))
            PvButton(
                text = stringResource(screenValues.manualSaveButtonLabel),
                onClick = onSave,
                enabled = !state.screenState.isSaving,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
