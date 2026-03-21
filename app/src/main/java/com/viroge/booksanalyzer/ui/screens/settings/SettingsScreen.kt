package com.viroge.booksanalyzer.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onOpenEntry: (route: String) -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(state.screenTitleRes),
            )
        },
    ) { screenPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding())
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
                .fillMaxSize(),
        ) {
            items(count = state.rowStates.size, key = { state.rowStates[it].id }) { position ->
                val entry = state.rowStates[position]

                HorizontalDivider(thickness = 0.8.dp)

                ListItem(
                    modifier = entry.route?.let { Modifier.clickable { onOpenEntry(it) } } ?: Modifier,

                    tonalElevation = if (entry.isHeader) 4.dp else 0.dp,
                    shadowElevation = 0.dp,

                    leadingContent =
                        if (entry.icon != null) {
                            { Icon(entry.icon, contentDescription = null) }
                        } else null,

                    headlineContent = {
                        entry.title?.let {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = it.asString(),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    },

                    supportingContent = entry.subtitle?.let {
                        {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = it.asString(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    },
                )
            }
        }
    }
}
