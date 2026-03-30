package com.viroge.booksanalyzer.ui.screens.books.library.collection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.viroge.booksanalyzer.ui.components.PvLinearProgressIndicator
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionLoadingScreen(
    screenValues: CollectionScreenValues,
    onBack: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current

    Scaffold(
        topBar = {
            PvTopAppBar(
                title = stringResource(screenValues.screenName),
                canGoBack = true,
                onBack = onBack,
            )
        }
    ) { screenPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .padding(bottom = appScaffoldPadding.calculateBottomPadding()),
        ) {
            // TODO: Later consider a Skeleton
            PvLinearProgressIndicator()
        }
    }
}
