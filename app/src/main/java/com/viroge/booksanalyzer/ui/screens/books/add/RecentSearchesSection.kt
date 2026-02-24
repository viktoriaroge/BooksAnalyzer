package com.viroge.booksanalyzer.ui.screens.books.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentSearchesSection(
    recent: List<String>,
    onPick: (String) -> Unit,
    onDeleteOne: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recent.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = stringResource(R.string.search_screen_recent_searches_section_text),
                style = MaterialTheme.typography.titleSmall,
            )

            TextButton(onClick = onClearAll) {
                Text(text = stringResource(R.string.search_screen_recent_searches_clear_button))
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        ) {

            recent.forEach { q ->
                RecentQueryChip(
                    query = q,
                    onPick = { onPick(q) },
                    onDelete = { onDeleteOne(q) },
                )
            }
        }
    }
}

@Composable
private fun RecentQueryChip(
    query: String,
    onPick: () -> Unit,
    onDelete: () -> Unit,
) {
    InputChip(
        selected = false,
        onClick = onPick,
        label = {
            Row(
                modifier = Modifier.heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = query,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        },
    )
}
