package com.viroge.booksanalyzer.ui.screens.books.cover

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    onRemoveInvalidUrl: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!state.isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
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
                    Box(modifier = Modifier.weight(1f)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(32.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(count = screenState.bookCovers.size, key = { idx -> screenState.bookCovers[idx].url }) { idx ->
                                val cover = screenState.bookCovers[idx]

                                Log.d("BookCoverPickerSheet", "Load Cover: ${cover.url}")

                                Box(modifier = Modifier.animateItem()) {
                                    CoverChoiceTile(
                                        cover = cover,
                                        selected = cover == screenState.selectedCover,
                                        onClick = { onSelect(cover.url) },
                                        onInvalidUrl = { onRemoveInvalidUrl(cover.url) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoverChoiceTile(
    cover: BookCoverPickerItem,
    selected: Boolean,
    onClick: () -> Unit,
    onInvalidUrl: () -> Unit,
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
            url = cover.url,
            imageSize = PvBookCoverImageSize.Large,

            reportOnLoadingError = cover.shouldReportOnFailToLoad,
            onLoadingError = onInvalidUrl,
        )
    }
}
