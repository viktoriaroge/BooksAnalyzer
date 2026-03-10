package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    state: ConfirmBookUiState,
    onTitleChange: (String) -> Unit,
    onAuthorsChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onIsbnChange: (String) -> Unit,
    onBack: () -> Unit,
    onOpenCoverPicker: () -> Unit,
    onSave: () -> Unit,
) {
    state.screenState.manualFormData ?: return
    val book = state.bookData ?: return
    val screenValues = state.screenState.screenValues

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
                .padding(screenPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.screenState.isSaving) {
                PvLinearProgressIndicator(modifier = Modifier.padding(top = 12.dp))
            }

            state.screenState.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            PvBookCoverHeader(
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
                value = state.screenState.titleInput,
                onValueChange = { onTitleChange(it) },
                label = { Text(stringResource(screenValues.manualTitleLabel)) },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.screenState.authorsInput,
                onValueChange = { onAuthorsChange(it) },
                label = { Text(stringResource(screenValues.manualAuthorLabel)) },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.screenState.yearInput,
                onValueChange = { onYearChange(it) },
                label = { Text(stringResource(screenValues.manualYearLabel)) },
                singleLine = true,
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.screenState.isbn13Input,
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
                enabled = !state.screenState.isSaving && state.screenState.titleInput.isNotBlank(),
            ) {
                Text(text = stringResource(screenValues.manualSaveButtonLabel))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
