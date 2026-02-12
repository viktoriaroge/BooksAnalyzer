package com.viroge.booksanalyzer.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppRoot() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelRoutes = setOf(
        Routes.LIBRARY,
        Routes.ADD_BOOK_FLOW,
        Routes.PROFILE,
    )

    val showBottomBar = currentDestination?.hierarchy?.any { it.route in topLevelRoutes } == true
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = rootPadding.calculateBottomPadding()),
        ) {
            AppNavHost(
                navController = navController,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
