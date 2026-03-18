package com.viroge.booksanalyzer.ui.screens.books.details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvButtonType
import com.viroge.booksanalyzer.ui.components.PvSkeletonArea
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverHeader
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding
import com.viroge.booksanalyzer.ui.screens.books.BookReadingStatusUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: BookDetailsScreenState.Content,
    onBack: () -> Unit,
    onStatusChange: (BookReadingStatusUi) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    val book = state.bookData
    val values = state.screenValues

    val scrollState = rememberScrollState()
    val scrollFraction = remember { derivedStateOf { (scrollState.value / 100f).coerceIn(0f, 1f) } }.value
    val appBarColor = lerp(
        start = Color.Transparent,
        stop = MaterialTheme.colorScheme.surface,
        fraction = scrollFraction
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(values.screenName),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                canGoBack = true,
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                        )
                    }
                },
            )
        },
    ) { _ ->

        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(bottom = appScaffoldPadding.calculateBottomPadding()),
        ) {

            PvBookCoverHeader(
                imageUrl = book.url,
                headersForBookCover = book.headers,
                // Animation parameters:
                animate = true,
                animationKey = book.animationKey,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            if (state.isLoading) {
                SkeletonLoader()

            } else {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(Modifier.height(height = 12.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = book.authors,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(height = 12.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = book.meta,
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(modifier = Modifier.height(height = 12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
                ) {
                    Text(
                        text = stringResource(values.originLabel),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    PvBookSourceBadge(
                        modifier = Modifier.padding(all = 2.dp),
                        sourceText = book.source.label.asString(),
                    )
                    Spacer(modifier = Modifier.weight(weight = 1f))
                }

                StatusPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    status = book.status,
                    onChange = onStatusChange,
                )

                Spacer(Modifier.height(height = 24.dp))
                PvButton(
                    buttonType = PvButtonType.Error,
                    text = stringResource(values.deleteButtonText),
                    icon = Icons.Default.Delete,
                    onClick = onDelete,
                    enabled = !state.isDeleting,
                )
            }

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusPicker(
    modifier: Modifier = Modifier,
    status: BookReadingStatusUi,
    onChange: (BookReadingStatusUi) -> Unit,
) {

    var expanded by remember { mutableStateOf(value = false) }
    val options = remember { BookReadingStatusUi.allOptions() }

    Column(modifier = modifier) {

        Spacer(Modifier.height(height = 24.dp))
        Text(
            text = stringResource(R.string.book_details_screen_status_label),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(Modifier.height(height = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {

            OutlinedTextField(
                value = status.label.asString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {

                options.forEach { status ->
                    DropdownMenuItem(text = { Text(text = status.label.asString()) }, onClick = {
                        expanded = false
                        onChange(status)
                    })
                }
            }
        }
    }
}

@Composable
private fun SkeletonLoader() {
    PvSkeletonArea(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        // Title
        Item(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
        )

        // Authors
        Spacer(Modifier.height(12.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
        )

        // Meta
        Spacer(Modifier.height(12.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
        )

        // Source Badge
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Item(
                modifier = Modifier
                    .width(40.dp)
                    .height(16.dp)
            )
            Item(
                modifier = Modifier
                    .width(80.dp)
                    .height(24.dp),
                cornerRadius = 12.dp
            )
        }

        // Status and Picker
        Spacer(Modifier.height(24.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp)
        )
        Spacer(Modifier.height(10.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )

        // Delete button
        Spacer(Modifier.height(18.dp))
        Item(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            cornerRadius = 24.dp,
        )
    }
}
