package com.viroge.booksanalyzer.ui.screens.terms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    state: TermsUiState,
    onBack: () -> Unit,
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(state.screenTitleRes),
                canGoBack = true,
                onBack = onBack,
            )
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {

            Spacer(Modifier.height(height = 24.dp))

            // All entries:
            PvItemCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Spacer(Modifier.height(height = 8.dp))

                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                    ) {
                        Image(
                            painterResource(state.introIconRes),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            modifier = Modifier,
                            text = customAnnotatedString(state.introTextRes),
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Spacer(Modifier.height(height = 24.dp))
                    HorizontalDivider(thickness = 1.dp)

                    state.rowStates.forEachIndexed { index, entry ->
                        TermEntry(
                            entry = entry,
                            addTopDivider = index != 0,
                        )
                    }
                }
            }

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}

@Composable
private fun TermEntry(
    entry: TermsRowState,
    addTopDivider: Boolean,
) {

    if (addTopDivider) {
        Spacer(Modifier.height(height = 12.dp))
        HorizontalDivider(thickness = 1.dp)
    }

    if (entry.showTitle) {
        Spacer(Modifier.height(height = 24.dp))

        val textColor = MaterialTheme.colorScheme.onSurface
        entry.title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = it,
                color = textColor,
                style = MaterialTheme.typography.titleLarge,
            )
        } ?: Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = customAnnotatedString(entry.titleRes),
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
        )
    }

    if (entry.showSubtitle) {
        Spacer(Modifier.height(height = 20.dp))

        val textColor = MaterialTheme.colorScheme.onSurface
        entry.subtitle?.let {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = it,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        } ?: Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = customAnnotatedString(entry.subtitleRes),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    Spacer(Modifier.height(height = 24.dp))
}
