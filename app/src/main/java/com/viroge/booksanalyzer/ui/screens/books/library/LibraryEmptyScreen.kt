package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@Composable
fun LibraryEmptyScreen(
    screenValues: LibraryScreenValues,
    emptyStateValues: EmptyStateValues,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(title = stringResource(screenValues.screenName))
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {

            Spacer(Modifier.height(height = 16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(emptyStateValues.emptyStateTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(height = 8.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(emptyStateValues.emptyStateText),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
