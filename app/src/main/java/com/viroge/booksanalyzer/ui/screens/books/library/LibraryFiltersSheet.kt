package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.domain.model.library.LibraryFilters
import com.viroge.booksanalyzer.domain.model.library.LibrarySort
import com.viroge.booksanalyzer.domain.model.ReadingStatus
import com.viroge.booksanalyzer.ui.screens.books.LibrarySortMapper
import com.viroge.booksanalyzer.ui.screens.books.StatusMapper

@Composable
fun LibraryFiltersSheet(
    filters: LibraryFilters,
    onStatusChange: (ReadingStatus?) -> Unit,
    onSortChange: (LibrarySort) -> Unit,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.library_filters_sheet_title),
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = onClear) { Text(text = stringResource(R.string.library_filters_sheet_button_clear_label)) }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Status selection: ----------------------------------------
        HorizontalDivider(thickness = 1.dp)
        ListItem(
            tonalElevation = 2.dp,
            leadingContent = { Icon(Icons.Default.FavoriteBorder, contentDescription = null) },
            headlineContent = {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = stringResource(R.string.library_filters_sheet_status_title),
                    style = MaterialTheme.typography.titleSmall,
                )
            },
        )
        HorizontalDivider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            FilterChip(
                selected = filters.status == null,
                onClick = { onStatusChange(null) },
                label = { Text(text = stringResource(R.string.search_mode_all)) }
            )

            ReadingStatus.entries.forEach { status ->
                FilterChip(
                    selected = filters.status == status,
                    onClick = { onStatusChange(status) },
                    label = { Text(text = StatusMapper.getUiModel(status).text) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Sort selection: -------------------------------------------------
        HorizontalDivider(thickness = 1.dp)
        ListItem(
            tonalElevation = 2.dp,
            leadingContent = { Icon(Icons.Default.LowPriority, contentDescription = null) },
            headlineContent = {
                Text(
                    text = stringResource(R.string.library_filters_sheet_sort_title),
                    style = MaterialTheme.typography.titleSmall,
                )
            },
        )
        HorizontalDivider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        LibrarySort.entries.forEachIndexed { index, sort ->
            if (index != 0) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    thickness = 1.dp,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            SortRadioRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                label = LibrarySortMapper.getUiModel(sort).text,
                selected = filters.sort == sort,
                onClick = { onSortChange(sort) },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SortRadioRow(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label)
        RadioButton(selected = selected, onClick = onClick)
    }
}