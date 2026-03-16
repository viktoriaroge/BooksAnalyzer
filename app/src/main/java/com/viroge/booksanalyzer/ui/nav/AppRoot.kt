package com.viroge.booksanalyzer.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.AppEvent
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel
import com.viroge.booksanalyzer.ui.common.util.truncate
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.components.snackbar.PvAppSnackbarController

@Composable
fun AppRoot() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelRoutes = setOf(
        Routes.LIBRARY,
        Routes.SEARCH_BOOK,
        Routes.SETTINGS,
    )

    val showBottomBar = currentDestination?.hierarchy?.any { it.route in topLevelRoutes } == true

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarController = remember(key1 = scope, key2 = snackbarHostState) {
        PvAppSnackbarController(scope, snackbarHostState)
    }

    CompositionLocalProvider(value = LocalAppSnackbar provides snackbarController) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {},
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                ) {
                    AppBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                // keeps one instance of each tab
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .consumeWindowInsets(innerPadding)
                ) {
                    AppSnackbarHandler()
                    AppNavHost(navController = navController)
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) { data ->
                    Snackbar(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        snackbarData = data,
                    )
                }
            }
        }
    }
}


@Composable
fun AppSnackbarHandler() {
    val sharedViewModel: MainSharedViewModel = activityViewModel()
    val snackbar = LocalAppSnackbar.current
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        sharedViewModel.events.collect { event ->
            val titleLimit = 44

            when (event) {
                is AppEvent.BookDeleted -> {
                    val normalizedTitle = event.title.truncate(limit = titleLimit)
                    snackbar.show(
                        message = context.getString(R.string.app_root_event_book_deleted, normalizedTitle),
                        actionLabel = context.getString(R.string.app_root_event_book_deleted_undo),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                        onActionPerformed = { sharedViewModel.undoMarkToDelete() },
                    )
                }

                is AppEvent.BookDeletingFailed -> {
                    val normalizedTitle = event.title.truncate(limit = titleLimit)
                    snackbar.show(
                        message = context.getString(R.string.app_root_event_book_delete_failed, normalizedTitle),
                        duration = SnackbarDuration.Short,
                    )
                }

                is AppEvent.BookRestoreSuccess -> {
                    val normalizedTitle = event.title.truncate(limit = titleLimit)
                    snackbar.show(
                        message = context.getString(R.string.app_root_event_book_restored, normalizedTitle),
                        duration = SnackbarDuration.Short,
                    )
                }

                is AppEvent.BookRestoreFailed -> {
                    val normalizedTitle = event.title.truncate(limit = titleLimit)
                    snackbar.show(
                        message = context.getString(R.string.app_root_event_book_restore_failed, normalizedTitle),
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }
}
