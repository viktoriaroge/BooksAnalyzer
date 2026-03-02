package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.CommonTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onOpenEntry: (route: String) -> Unit,
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CommonTopAppBar(
                title = stringResource(state.screenTitleRes),
            )
        },
    ) { screenPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding())
                .fillMaxSize(),
        ) {
            items(items = state.rowStates) { item ->

                HorizontalDivider(thickness = 1.dp)

                ListItem(
                    tonalElevation = if (item.isHeader) 2.dp else 0.dp,
                    leadingContent =
                        if (item.icon != null) {
                            { Icon(item.icon, contentDescription = null) }
                        } else null,
                    headlineContent = {
                        if (item.showTitle) {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = item.title ?: stringResource(item.titleRes),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    },
                    supportingContent =
                        if (item.showSubtitle) {
                            {
                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    text = item.subtitle ?: stringResource(item.subtitleRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        } else null,
                    modifier = Modifier.clickable { item.route?.let { onOpenEntry(it) } }
                )
            }
        }
    }
}