package com.viroge.booksanalyzer.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viroge.booksanalyzer.ui.AppEvent
import com.viroge.booksanalyzer.ui.MainSharedViewModel
import com.viroge.booksanalyzer.ui.activityViewModel
import com.viroge.booksanalyzer.ui.common.AppBottomBar
import com.viroge.booksanalyzer.ui.snackbar.AppSnackbarController
import com.viroge.booksanalyzer.ui.snackbar.LocalAppSnackbar

@Composable
fun AppRoot() {

    val sharedViewModel: MainSharedViewModel = activityViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelRoutes = setOf(
        Routes.LIBRARY,
        Routes.ADD_BOOK,
        Routes.PROFILE,
    )

    val showBottomBar = currentDestination?.hierarchy?.any { it.route in topLevelRoutes } == true

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarController = remember(key1 = scope, key2 = snackbarHostState) {
        AppSnackbarController(scope, snackbarHostState)
    }

    CompositionLocalProvider(value = LocalAppSnackbar provides snackbarController) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
        ) { rootPadding ->

            val snackbar = LocalAppSnackbar.current

            LaunchedEffect(key1 = Unit) {
                sharedViewModel.events.collect { event ->
                    when (event) {
                        is AppEvent.BookDeleted -> {
                            snackbar.show(
                                message = "Book \"${event.title}\" deleted.",
                                actionLabel = "Undo",
                                withDismissAction = true,
                                duration = SnackbarDuration.Long,
                                onActionPerformed = { sharedViewModel.undoMarkToDelete() },
                            )
                        }

                        is AppEvent.BookDeletingFailed -> {
                            snackbar.show(
                                message = "Deleting book \"${event.title}\" failed.",
                                duration = SnackbarDuration.Short,
                            )
                        }

                        is AppEvent.BookRestoreFailed -> {
                            snackbar.show(
                                message = "Restoring book \"${event.title}\" failed.",
                                duration = SnackbarDuration.Short,
                            )
                        }

                        is AppEvent.BookRestoreSuccess -> {
                            snackbar.show(
                                message = "Book \"${event.title}\" restored.",
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = rootPadding.calculateBottomPadding()),
            ) {
                AppNavHost(navController = navController)
            }
        }
    }
}
