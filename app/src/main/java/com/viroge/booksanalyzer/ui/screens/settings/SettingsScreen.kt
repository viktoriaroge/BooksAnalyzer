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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onOpenEntry: (route: String) -> Unit,
) {


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { screenPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding())
                .fillMaxSize(),
        ) {
            items(items = state.settingsEntries, key = { it.title }) { item ->

                HorizontalDivider(thickness = 1.dp)

                val textColor =
                    if (item.isEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                ListItem(
                    tonalElevation = if (item.isHeader) 2.dp else 0.dp,
                    leadingContent = { item.icon?.let { Icon(it, contentDescription = null) } },
                    headlineContent = {
                        Text(
                            text = item.title,
                            color = textColor,
                        )
                    },
                    supportingContent = {
                        item.subtitle?.let {
                            Text(
                                text = it,
                                color = textColor,
                            )
                        }
                    },
                    modifier = Modifier.clickable { item.route?.let { onOpenEntry(it) } }
                )
            }
        }
    }
}