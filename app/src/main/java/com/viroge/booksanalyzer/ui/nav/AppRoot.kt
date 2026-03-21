package com.viroge.booksanalyzer.ui.nav

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viroge.booksanalyzer.R
import com.viroge.booksanalyzer.ui.common.action.BookActionEvent
import com.viroge.booksanalyzer.ui.common.action.BookActionViewModel
import com.viroge.booksanalyzer.ui.common.util.truncate
import com.viroge.booksanalyzer.ui.components.snackbar.LocalAppSnackbar
import com.viroge.booksanalyzer.ui.components.snackbar.PvAppSnackbarController

val LocalAppScaffoldPadding = staticCompositionLocalOf { PaddingValues(0.dp) }

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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            // Make the bar background fully transparent
            val window = (view.context as Activity).window
            window.navigationBarColor = Color.TRANSPARENT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(navController, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navController.currentBackStackEntryFlow.collect { entry ->
                Log.d("AppRoot", "NAV_DEBUG: Current Route: ${entry.destination.route}")
                Log.d("AppRoot", "NAV_DEBUG: Arguments: ${entry.arguments}")
            }
        }
    }

    CompositionLocalProvider(value = LocalAppSnackbar provides snackbarController) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {},
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(tween(400)) { it },
                    exit = slideOutVertically(tween(400)) { it },
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

            CompositionLocalProvider(LocalAppScaffoldPadding provides innerPadding) {
                Box(modifier = Modifier.fillMaxSize()) {

                    AppNavHost(navController = navController)

                    AppSnackbarHandler()
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

                    // Bottom System Bar background:
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AppSnackbarHandler() {
    val snackbar = LocalAppSnackbar.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val activity = LocalContext.current as? ComponentActivity
        ?: error("This Composable must be hosted in a ComponentActivity")
    val bookActionVM: BookActionViewModel = hiltViewModel(activity)

    LaunchedEffect(bookActionVM.events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            bookActionVM.events.collect { event ->
                val titleLimit = 44
                fun String.limit() = this.truncate(titleLimit)

                when (event) {
                    is BookActionEvent.BookDeleted -> {
                        snackbar.show(
                            message = context.getString(R.string.app_root_event_book_deleted, event.title.limit()),
                            actionLabel = context.getString(R.string.app_root_event_book_deleted_undo),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long,
                            onActionPerformed = { bookActionVM.undoMarkToDelete() },
                        )
                    }

                    is BookActionEvent.BookDeletingFailed -> {
                        snackbar.show(
                            message = context.getString(R.string.app_root_event_book_delete_failed, event.title.limit()),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is BookActionEvent.BookRestoreSuccess -> {
                        snackbar.show(
                            message = context.getString(R.string.app_root_event_book_restored, event.title.limit()),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is BookActionEvent.BookRestoreFailed -> {
                        snackbar.show(
                            message = context.getString(R.string.app_root_event_book_restore_failed, event.title.limit()),
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
        }
    }
}
