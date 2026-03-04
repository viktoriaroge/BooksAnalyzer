package com.viroge.booksanalyzer.ui.screens.books.confirm

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.domain.model.library.SearchMode
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookScreen(
    state: ConfirmBookUiState,
    prefillQuery: String?,
    prefillMode: SearchMode?,
    onOpenCoverPicker: () -> Unit,
    onBack: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmSaveManual: (String, String, Int?, String?, String?) -> Unit,
) {
    val values = state.screenValues
    val book = state.bookData

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(if (book != null) values.screenTitleConfirm else values.screenTitleManual),
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
            if (state.isSaving) {
                PvLinearProgressIndicator(modifier = Modifier.padding(top = 12.dp))
            }

            state.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (book != null) {
                // Mode: Confirming an existing result
                BookCoverHeader(
                    imageUrl = book.coverUrl,
                    headersForBookCover = book.coverHeaders,
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
                    PvBookSourceBadge(sourceTextRes = book.sourceBadgeTextRes)
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = onConfirmSave,
                    enabled = !state.isSaving,
                ) {
                    Text(text = stringResource(values.saveButtonLabel))
                }
            } else if (!prefillQuery.isNullOrBlank()) {
                // Mode: Manual Form
                ConfirmBookManualForm(
                    prefillQuery = prefillQuery,
                    prefillMode = prefillMode ?: SearchMode.ALL,
                    isSaving = state.isSaving,
                    onSave = onConfirmSaveManual,
                )
            }
        }
    }
}

@Composable
fun BookCoverHeader(
    imageUrl: String?,
    headersForBookCover: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds() // Prevents the blur from bleeding out
    ) {
        // Hazy background:
        val isDarkTheme = isSystemInDarkTheme()
        PvBookCoverAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 30.dp)
                .drawWithContent {
                    drawContent()
                    // Adjust slightly so the foreground pops
                    drawRect(
                        if (isDarkTheme) Color.Black.copy(alpha = 0.3f)
                        else Color.White.copy(alpha = 0.3f)
                    )
                },
            contentScale = ContentScale.Crop,
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = PvBookCoverImageSize.XXLARGE,
        )

        // Cover image:
        PvBookCoverAsyncImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 32.dp)
                .shadow(12.dp, RoundedCornerShape(12.dp)),
            url = imageUrl,
            requestHeaders = headersForBookCover,
            size = PvBookCoverImageSize.XXLARGE,
        )
    }
}
