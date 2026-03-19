package com.viroge.booksanalyzer.ui.screens.books.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.viroge.booksanalyzer.ui.components.PvButton
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverAsyncImage
import com.viroge.booksanalyzer.ui.components.bookcover.PvBookCoverImageSize
import com.viroge.booksanalyzer.ui.components.bookcover.PvHazyBookCoverBackground
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding
import kotlin.math.absoluteValue

@Composable
fun LibraryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: LibraryScreenState.Content,
    activeBook: LibraryBookData?,
    values: ContentStateValues,
    pagerState: PagerState,
    onOpenCollection: () -> Unit,
    onOpenBook: (String) -> Unit,
) {
    val scaffoldPadding = LocalAppScaffoldPadding.current
    val bottomBarHeight = scaffoldPadding.calculateBottomPadding()

    val scrollState = rememberScrollState()
    val scrollFraction = remember { derivedStateOf { (scrollState.value / 100f).coerceIn(0f, 1f) } }.value
    val appBarColor = lerp(
        start = Color.Transparent,
        stop = MaterialTheme.colorScheme.surface,
        fraction = scrollFraction
    )

    Scaffold(
        topBar = {
            PvTopAppBar(
                showLogo = true,
                title = stringResource(values.screenName),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    IconButton(onClick = onOpenCollection) {
                        Icon(
                            imageVector = Icons.Default.LocalLibrary,
                            contentDescription = "",
                        )
                    }
                },
            )
        },
    ) { _ ->

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {

                // Selected (active) Book's Cover as Top Background:
                activeBook?.let {
                    PvHazyBookCoverBackground(
                        modifier = Modifier.align(Alignment.TopCenter),
                        headerCoverSize = PvBookCoverImageSize.XXLarge,
                        imageUrl = it.url,
                        headersForBookCover = it.headers,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {

                    Spacer(Modifier.height(120.dp))

                    // Carousel with Book Covers
                    CurrentBooksCarousel(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        pagerState = pagerState,
                        books = state.currentBooks,
                        onBookClick = { onOpenBook(it.id) },
                    )

                    Spacer(Modifier.height(32.dp))

                    // Selected (active) Book's content:
                    CurrentBookContent(
                        values = values,
                        activeBook = activeBook,
                        onOpenBook = onOpenBook,
                    )
                }
            }

            Spacer(Modifier.height(height = bottomBarHeight + 24.dp))
        }
    }
}

@Composable
private fun CurrentBooksCarousel(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    pagerState: PagerState,
    books: List<LibraryBookData>,
    onBookClick: (LibraryBookData) -> Unit
) {
    val imageSize = PvBookCoverImageSize.XXLarge

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 64.dp),
        pageSpacing = (-16).dp,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .height(imageSize.height),
    ) { page ->
        val book = books[page]
        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = lerp(
                            start = 0.7f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = scale
                        scaleY = scale

                        alpha = lerp(
                            start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        translationX = 0f
                    }
                    .zIndex(1f - pageOffset)
                    .width(imageSize.width)
                    .height(imageSize.height),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                onClick = { onBookClick(book) },
            ) {
                PvBookCoverAsyncImage(
                    url = book.url,
                    requestHeaders = book.headers,
                    imageSize = imageSize,
                    // Animation parameters:
                    animate = true,
                    animationKey = book.animationKey,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        }
    }
}

@Composable
private fun CurrentBookContent(
    values: ContentStateValues,
    activeBook: LibraryBookData?,
    onOpenBook: (String) -> Unit
) {
    AnimatedContent(
        targetState = activeBook,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(), label = "BookDetails"
    ) { book ->
        if (book != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = book.title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(8.dp))
                if (book.authors.isNotEmpty()) {
                    Text(
                        text = book.authors,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(32.dp))
                PvLinearProgressIndicator(modifier = Modifier.padding(horizontal = 32.dp), progress = { 0.3F })

                Spacer(Modifier.height(4.dp))
                PvButton(
                    text = stringResource(values.startReadingButtonText),
                    icon = Icons.Default.PlayArrow,
                    onClick = remember { { onOpenBook(book.id) } },
                )
            }
        }
    }
}
