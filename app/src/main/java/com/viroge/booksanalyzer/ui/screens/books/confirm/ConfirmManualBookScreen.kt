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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

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
    if (!state.screenState.isInManualMode) return
    val book = state.bookData ?: return

    val screenValues = state.screenState.screenValues
    val editState = state.screenState.editState

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(screenValues.screenTitleManual),
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(top = screenPadding.calculateTopPadding()),
        ) {
            if (state.screenState.isSaving) {
                PvLinearProgressIndicator(modifier = Modifier.padding(top = 12.dp))
            }

            PvBookCoverHeader(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                animationKey = book.animationKey,
                imageUrl = book.url,
                headersForBookCover = book.headers,
            )

            Button(
                onClick = { onOpenCoverPicker() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(text = stringResource(screenValues.changeCoverButtonLabel))
            }

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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = { onSave() },
            ) {
                Text(text = stringResource(screenValues.manualSaveButtonLabel))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
