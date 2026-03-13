package com.viroge.booksanalyzer.ui.nav

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.screens.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.screens.books.deleted.RecentlyDeletedRoute
import com.viroge.booksanalyzer.ui.screens.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.screens.books.library.LibraryRoute
import com.viroge.booksanalyzer.ui.screens.books.search.SearchBookRoute
import com.viroge.booksanalyzer.ui.screens.settings.SettingsRoute
import com.viroge.booksanalyzer.ui.screens.terms.TermsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
) {

    fun navigateSafe(
        route: String,
        navOptions: (NavOptionsBuilder.() -> Unit)? = null,
    ) {
        if (navController.currentBackStackEntry?.destination?.route != route) {
            navController.navigate(route, navOptions ?: {})
        }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Routes.LIBRARY_GRAPH,
        ) {

            // --- LIBRARY TAB --------------------------------------------------------------

            navigation(
                route = Routes.LIBRARY_GRAPH,
                startDestination = Routes.LIBRARY
            ) {
                composable(Routes.LIBRARY) {
                    LibraryRoute(
                        onOpenBook = { navigateSafe(Routes.BOOK_DETAILS) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(Routes.BOOK_DETAILS) {
                    BookDetailsRoute(
                        onBack = { navController.popBackStack() },
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
                        onBack = { navController.popBackStack() },
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
                        onBack = { navController.popBackStack() },
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
                        onBack = { navController.popBackStack() },
                    )
                }

                composable(Routes.APP_TERMS) {
                    TermsRoute(
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
