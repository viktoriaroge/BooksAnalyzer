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
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverImageSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCoverPickerSheet(
    state: BookCoverPickerUiState,
    onManualUrlChange: (String) -> Unit,
    onAddManualUrl: () -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!state.isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
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
                text = stringResource(state.screenValues.screenTitle),
                style = MaterialTheme.typography.titleLarge
            )

            when (val screenState = state.screenState) {
                BookCoverPickerScreenState.Loading -> {
                    // Empty screen is fine for now since loading is almost instant.
                }

                is BookCoverPickerScreenState.Content -> {
                    // Manual url input:
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = screenState.manualUrlInput,
                        onValueChange = onManualUrlChange,
                        label = { Text(stringResource(screenState.values.inputFieldLabel)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = onAddManualUrl) {
                                Icon(screenState.values.inputFieldIcon, contentDescription = "")
                            }
                        }
                    )

                    // Grid with image candidates:
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(screenState.bookCovers.size) { idx ->
                            val cover = screenState.bookCovers[idx]
                            CoverChoiceTile(
                                url = cover.url,
                                selected = cover == screenState.selectedCover,
                                onClick = { onSelect(cover.url) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CoverChoiceTile(
    url: String,
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
            imageSize = PvBookCoverImageSize.Large,
        )
    }
}
