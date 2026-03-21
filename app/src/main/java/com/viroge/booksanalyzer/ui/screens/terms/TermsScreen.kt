package com.viroge.booksanalyzer.ui.screens.terms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.common.util.customAnnotatedString
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    state: TermsUiState,
    onBack: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current
    val topShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val bottomShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    val middleShape = RoundedCornerShape(0.dp)
    val tonalElevation: Dp = 4.dp

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = screenPadding.calculateTopPadding())
                .padding(bottom = appScaffoldPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
        ) {
            item {
                Surface(
                    shape = topShape,
                    tonalElevation = tonalElevation,
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        ) {
                            Image(
                                modifier = Modifier.size(100.dp),
                                painter = painterResource(state.introIconRes),
                                contentDescription = "",
                                contentScale = ContentScale.FillWidth,
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = customAnnotatedString(state.introTextRes),
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 24.dp))
                    }
                }
            }

            items(count = state.rowStates.size, key = { state.rowStates[it].id }) { position ->
                val isLast = position == state.rowStates.lastIndex
                val entry = state.rowStates[position]

                Surface(
                    shape = if (isLast) bottomShape else middleShape,
                    tonalElevation = tonalElevation,
                ) {
                    Column {
                        TermEntry(entry)

                        if (isLast) Spacer(modifier = Modifier.height(height = 24.dp))
                        else HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TermEntry(
    entry: TermsRowState,
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    entry.title?.let {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = customAnnotatedString(it.asString()),
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
        )
    }

    if (entry.title != null && entry.subtitle != null) {
        Spacer(Modifier.height(height = 24.dp))
    }

    entry.subtitle?.let {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = customAnnotatedString(it.asString()),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
