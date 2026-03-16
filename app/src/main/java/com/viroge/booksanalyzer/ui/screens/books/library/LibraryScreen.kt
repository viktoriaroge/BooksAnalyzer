package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.viroge.booksanalyzer.ui.components.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.PvBookSourceBadge
import com.viroge.booksanalyzer.ui.components.PvItemCard
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar

@Composable
fun LibraryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: LibraryScreenState.Content,
    screenValues: LibraryScreenValues,
    values: ContentStateValues,
    currentListState: LazyListState,
    onToggleLibraryView: () -> Unit,
    onOpenBook: (String) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(screenValues.screenName),
            )
        },
        floatingActionButton = {
            LibraryFloatingActionButton(
                isFullLibrary = false,
                fabShowFullText = stringResource(values.fullCollectionFabText),
                onClick = onToggleLibraryView,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .fillMaxSize(),
        ) {
            if (state.currentBooks.isNotEmpty()) {
                LazyRow(
                    state = currentListState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                ) {
                    items(
                        items = state.currentBooks,
                        key = { it.id },
                    ) { book ->
                        CurrentlyReadingCard(
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            book = book,
                            onClick = { onOpenBook(book.id) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(height = 24.dp))
        }
    }
}

@Composable
private fun CurrentlyReadingCard(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    book: LibraryBookData,
    onClick: () -> Unit
) {
    PvItemCard(
        modifier = Modifier.width(width = 210.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PvBookCoverAsyncImage(
                    url = book.url,
                    requestHeaders = book.headers,
                    imageSize = PvBookCoverImageSize.Medium,
                    modifier = Modifier.align(Alignment.Center),
                    // Animation parameters:
                    animate = true,
                    animationKey = book.animationKey,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )

                PvBookSourceBadge(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(all = 2.dp),
                    sourceText = book.source.shortLabel.asString(),
                )
            }

            PvLinearProgressIndicator(progress = { 0.3F })

            Text(
                text = book.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (book.authors.isNotEmpty()) {
                Text(
                    text = book.authors,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
