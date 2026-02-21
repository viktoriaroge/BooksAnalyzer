package com.viroge.booksanalyzer.ui.books.cover

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.common.CommonAsyncImage
import com.viroge.booksanalyzer.ui.common.CommonAsyncImageSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverPickerSheet(
    state: CoverPickerUiState,
    selectedUrl: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!state.isOpen) return

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Choose a cover", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
                Spacer(Modifier.height(24.dp))
                return@ModalBottomSheet
            }

            // Simple grid using LazyVerticalGrid
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
        CommonAsyncImage(
            modifier = Modifier.fillMaxSize(),
            url = url,
            requestHeaders = requestHeaders,
            size = CommonAsyncImageSize.LARGE,
        )
    }
}
