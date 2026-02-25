package com.viroge.booksanalyzer.ui.components.bookcover

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.components.CommonCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.CommonAsyncImageSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverPickerSheet(
    state: CoverPickerUiState,
    selectedUrl: String?,
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
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.book_cover_picker_name), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            // Manual url input:
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.manualUrlInput,
                    onValueChange = onManualUrlChange,
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.book_cover_picker_input_field_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(onClick = onAddManualUrl) {
                    Icon(Icons.Default.Check, contentDescription = "")
                }
            }

            Spacer(Modifier.height(12.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(state.candidates.size) { idx ->
                    val candidate = state.candidates[idx]
                    CoverChoiceTile(
                        url = candidate.first,
                        requestHeaders = candidate.second,
                        selected = (candidate.first == selectedUrl),
                        onClick = { onSelect(candidate.first) }
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
        CommonCoverAsyncImage(
            modifier = Modifier.fillMaxSize(),
            url = url,
            requestHeaders = requestHeaders,
            size = CommonAsyncImageSize.LARGE,
        )
    }
}
