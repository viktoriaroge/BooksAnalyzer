package com.viroge.booksanalyzer.ui.screens.books.details

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsEditScreen(
    state: BookDetailsScreenState.Edit,
    onSaveEdits: () -> Unit,
    onCancelEdit: () -> Unit,
    onUpdateEditTitle: (String) -> Unit,
    onUpdateEditAuthors: (String) -> Unit,
    onUpdateEditPublishedYear: (String) -> Unit,
    onUpdateEditIsbn13: (String) -> Unit,
    onUpdateEditIsbn10: (String) -> Unit,
    onOpenCoverPicker: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    val book = state.bookData
    val values = state.editStateValues
    val editState = state.editState

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
                title = stringResource(values.screenName),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                canGoBack = true,
                onBack = onCancelEdit,
                actions = {
                    IconButton(onClick = onSaveEdits) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                        )
                    }
                },
            )
        }) { _ ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
                .imePadding(),
        ) {

            PvBookCoverHeader(
                imageUrl = book.url,
                headersForBookCover = book.headers,
                modifier = Modifier.fillMaxWidth(),
            )

            PvButton(
                buttonType = PvButtonType.Secondary,
                text = stringResource(values.changeCoverButtonText),
                onClick = onOpenCoverPicker,
            )

            Spacer(Modifier.height(height = 24.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editTitle,
                onValueChange = onUpdateEditTitle,
                label = { Text(stringResource(values.titleLabel)) },
                singleLine = true,
                isError = editState.showTitleError,
                supportingText = {
                    if (editState.showTitleError) {
                        Text(
                            text = stringResource(values.titleError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editAuthors,
                onValueChange = onUpdateEditAuthors,
                label = { Text(stringResource(values.authorLabel)) },
                singleLine = true,
                placeholder = { Text(stringResource(values.authorHint)) },
                isError = editState.showAuthorError,
                supportingText = {
                    if (editState.showAuthorError) {
                        Text(
                            text = stringResource(values.authorError),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editYear,
                onValueChange = onUpdateEditPublishedYear,
                label = { Text(stringResource(values.yearLabel)) },
                singleLine = true,
                placeholder = { Text(stringResource(values.yearHint)) },
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editIsbn13,
                onValueChange = onUpdateEditIsbn13,
                label = { Text(stringResource(values.isbn13Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(height = 12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = editState.editIsbn10,
                onValueChange = onUpdateEditIsbn10,
                label = { Text(stringResource(values.isbn10Label)) },
                singleLine = true,
            )

            Spacer(Modifier.height(height = 16.dp))

            PvButton(
                text =
                    if (state.isSaving) stringResource(values.saveChangesInProgressButtonText)
                    else stringResource(values.saveChangesButtonText),
                onClick = onSaveEdits,
                enabled = !state.isSaving,
            )

            Spacer(Modifier.height(height = 12.dp))
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onCancelEdit,
            ) {
                Text(stringResource(values.cancelChangesButtonText))
            }

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}
