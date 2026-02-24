package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import com.viroge.booksanalyzer.domain.LibraryFilters
import com.viroge.booksanalyzer.domain.LibrarySort
import com.viroge.booksanalyzer.domain.ReadingStatus
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
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.library_filters_sheet_title), style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClear) { Text(text = stringResource(R.string.library_filters_sheet_button_clear_label)) }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            thickness = 1.dp,
        )

        Text(text = stringResource(R.string.library_filters_sheet_status_title), style = MaterialTheme.typography.titleSmall)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            thickness = 1.dp,
        )

        Text(
            text = stringResource(R.string.library_filters_sheet_sort_title),
            style = MaterialTheme.typography.titleSmall,
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = stringResource(R.string.library_filters_sheet_sort_by_added_label),
            selected = filters.sort == LibrarySort.ADDED,
            onClick = { onSortChange(LibrarySort.ADDED) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = stringResource(R.string.library_filters_sheet_sort_by_recent_label),
            selected = filters.sort == LibrarySort.RECENT,
            onClick = { onSortChange(LibrarySort.RECENT) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = stringResource(R.string.library_filters_sheet_sort_by_title_label),
            selected = filters.sort == LibrarySort.TITLE,
            onClick = { onSortChange(LibrarySort.TITLE) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright,
            thickness = 1.dp,
        )
        SortRadioRow(
            label = stringResource(R.string.library_filters_sheet_sort_by_author_label),
            selected = filters.sort == LibrarySort.AUTHOR,
            onClick = { onSortChange(LibrarySort.AUTHOR) },
        )
    }
}

@Composable
private fun SortRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        RadioButton(selected = selected, onClick = onClick)
    }
}