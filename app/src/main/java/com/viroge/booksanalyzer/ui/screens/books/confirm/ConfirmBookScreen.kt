package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    state: ConfirmBookUiState,
    onOpenCoverPicker: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    val book = state.bookData ?: return
    val values = state.screenState.screenValues

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenTitleConfirm),
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
                onClick = onOpenCoverPicker,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(text = stringResource(values.changeCoverButtonLabel))
            }

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

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onSave,
                enabled = !state.screenState.isSaving,
            ) {
                Text(text = stringResource(values.saveButtonLabel))
            }
        }
    }
}
