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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Save
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
    screenState: ConfirmBookScreenState.ManualInput,
    onTitleChange: (String) -> Unit,
    onAuthorsChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onIsbnChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenCoverPicker: () -> Unit,
    onSave: () -> Unit,
) {
    val book = screenState.bookData ?: return
    val values = screenState.screenValues

    val appScaffoldPadding = LocalAppScaffoldPadding.current

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
                title = stringResource(values.screenTitle),
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
            if (screenState.isSaving) {
                PvLinearProgressIndicator(modifier = Modifier.padding(top = 12.dp))
            }

            PvBookCoverHeader(
                imageUrl = book.coverUrl,
                // Animation parameters:
                animate = true,
                animationKey = book.animationKey,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            PvButton(
                buttonType = PvButtonType.Secondary,
                text = stringResource(values.changeCoverButtonLabel),
                icon = Icons.Default.ImageSearch,
                onClick = onOpenCoverPicker,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(values.manualInstruction),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = screenState.editTitle,
                onValueChange = { onTitleChange(it) },
                label = { Text(stringResource(values.manualTitleLabel)) },
                singleLine = true,
                isError = screenState.showTitleError,
                supportingText = {
                    if (screenState.showTitleError) {
                        Text(
                            text = stringResource(values.manualTitleError),
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
                value = screenState.editAuthors,
                onValueChange = { onAuthorsChange(it) },
                label = { Text(stringResource(values.manualAuthorLabel)) },
                singleLine = true,
                isError = screenState.showAuthorError,
                supportingText = {
                    if (screenState.showAuthorError) {
                        Text(
                            text = stringResource(values.manualAuthorError),
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
                value = screenState.editYear,
                onValueChange = { onYearChange(it) },
                label = { Text(stringResource(values.manualYearLabel)) },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = screenState.editIsbn13,
                onValueChange = { onIsbnChange(it) },
                label = { Text(stringResource(values.manualIsbn13Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(16.dp))
            PvButton(
                text = stringResource(values.manualSaveButtonLabel),
                icon = Icons.Default.Save,
                onClick = onSave,
                enabled = !screenState.isSaving,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
