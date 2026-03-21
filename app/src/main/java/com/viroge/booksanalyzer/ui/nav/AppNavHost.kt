package com.viroge.booksanalyzer.ui.nav

import android.util.Log
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.screens.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.screens.books.deleted.RecentlyDeletedRoute
import com.viroge.booksanalyzer.ui.screens.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.screens.books.library.LibraryRoute
import com.viroge.booksanalyzer.ui.screens.books.library.collection.CollectionRoute
import com.viroge.booksanalyzer.ui.screens.books.search.SearchBookRoute
import com.viroge.booksanalyzer.ui.screens.settings.SettingsRoute
import com.viroge.booksanalyzer.ui.screens.terms.TermsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
) {

    fun navigateSafe(
        route: String,
        isTabSwitch: Boolean = false,
        navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null,
    ) {
        val isAlreadyThere = navController.currentDestination?.hierarchy?.any { it.route == route } == true

        if (!isAlreadyThere) {
            navController.navigate(route) {
                if (isTabSwitch) {
                    // Standard Tab Switching logic
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                } else {
                    // Custom options (like popUpTo) if provided
                    navOptionsBuilder?.invoke(this)
                }
            }
        } else {
            Log.d("AppNavHost", "NAV_DEBUG: Navigation blocked: Already at or inside $route")
        }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.LIBRARY_GRAPH,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
        ) {

            // --- LIBRARY TAB --------------------------------------------------------------

            navigation(
                route = Routes.LIBRARY_GRAPH,
                startDestination = Routes.LIBRARY
            ) {
                composable(Routes.LIBRARY) {
                    LibraryRoute(
                        onOpenSearch = {
                            navigateSafe(route = Routes.SEARCH_BOOK_GRAPH, isTabSwitch = true)
                        },
                        onOpenCollection = { navigateSafe(Routes.COLLECTION) },
                        onOpenBook = { navigateSafe(Routes.BOOK_DETAILS) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(Routes.COLLECTION) {
                    CollectionRoute(
                        onBack = navController::popBackStack,
                        onOpenSearch = {
                            navController.popBackStack(route = Routes.LIBRARY, inclusive = false)
                            navigateSafe(route = Routes.SEARCH_BOOK_GRAPH, isTabSwitch = true)
                        },
                        onOpenBook = { navigateSafe(Routes.BOOK_DETAILS) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(Routes.BOOK_DETAILS) {
                    BookDetailsRoute(
                        onBack = navController::popBackStack,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }
            }

            // --- SEARCH BOOK TAB -------------------------------------------------------------

            navigation(
                route = Routes.SEARCH_BOOK_GRAPH,
                startDestination = Routes.SEARCH_BOOK
            ) {
                composable(Routes.SEARCH_BOOK) {
                    SearchBookRoute(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onGoToConfirm = { navigateSafe(Routes.CONFIRM_BOOK) },
                    )
                }

                composable(Routes.CONFIRM_BOOK) {
                    ConfirmBookRoute(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onBack = navController::popBackStack,
                        onBookSaved = {
                            navigateSafe(Routes.BOOK_DETAILS) {
                                popUpTo(Routes.SEARCH_BOOK) { inclusive = false }
                            }
                        },
                    )
                }

                composable(Routes.BOOK_DETAILS) {
                    BookDetailsRoute(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onBack = navController::popBackStack,
                    )
                }
            }

            // --- SETTINGS TAB ------------------------------------------------------------

            navigation(
                route = Routes.SETTINGS_GRAPH,
                startDestination = Routes.SETTINGS
            ) {
                composable(Routes.SETTINGS) {
                    SettingsRoute(
                        onOpenEntry = navController::navigate,
                    )
                }

                composable(Routes.RECENTLY_DELETED_BOOKS) {
                    RecentlyDeletedRoute(
                        onBack = navController::popBackStack,
                    )
                }

                composable(Routes.APP_TERMS) {
                    TermsRoute(
                        onBack = navController::popBackStack,
                    )
                }
            }
        }
    }
}
