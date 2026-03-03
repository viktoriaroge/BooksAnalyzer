package com.viroge.booksanalyzer.ui.screens.books.cover

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverPickerSheet(
    state: BookCoverPickerUiState,
    onManualUrlChange: (String) -> Unit,
    onAddManualUrl: () -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!state.isOpen) return

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp),
                text = stringResource(state.screenValues.screenTitle), style = MaterialTheme.typography.titleLarge
            )

            // Manual url input:
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.manualUrlInput,
                onValueChange = onManualUrlChange,
                label = { Text(stringResource(state.screenValues.inputFieldLabel)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = onAddManualUrl) {
                        Icon(state.screenValues.inputFieldIcon, contentDescription = "")
                    }
                }
            )

            if (state.isLoading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(24.dp))
                return@ModalBottomSheet
            }

            // Grid with image candidates:
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.bookCovers.size) { idx ->
                    val candidate = state.bookCovers[idx]
                    CoverChoiceTile(
                        url = candidate.url,
                        requestHeaders = candidate.headers,
                        selected = candidate == state.selectedCover,
                        onClick = { onSelect(candidate.url) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CoverChoiceTile(
    url: String,
    requestHeaders: Map<String, String>,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(0.66f) // book-ish ratio
            .then(
                if (selected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                )
                else Modifier
            )
    ) {
        PvBookCoverAsyncImage(
            modifier = Modifier.fillMaxSize(),
            url = url,
            requestHeaders = requestHeaders,
            size = PvBookCoverImageSize.LARGE,
        )
    }
}
