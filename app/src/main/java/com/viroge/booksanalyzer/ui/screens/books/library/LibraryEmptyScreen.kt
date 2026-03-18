package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@Composable
fun LibraryEmptyScreen(
    screenValues: LibraryScreenValues,
    emptyStateValues: EmptyStateValues,
    actionIcon: ImageVector,
    onAction: () -> Unit,
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(height = 16.dp))
            Image(
                modifier = Modifier
                    .size(260.dp)
                    .padding(horizontal = 24.dp),
                painter = painterResource(R.drawable.ic_default_book),
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
            )

            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(emptyStateValues.emptyStateTitle),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(height = 24.dp))
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = customAnnotatedString(emptyStateValues.emptyStateText),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(24.dp))
            PvButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(emptyStateValues.emptyStateButton),
                icon = actionIcon,
                onClick = onAction,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
