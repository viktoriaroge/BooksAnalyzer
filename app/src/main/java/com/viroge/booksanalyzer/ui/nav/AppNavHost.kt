package com.viroge.booksanalyzer.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.screens.books.search.SearchBookRoute
import com.viroge.booksanalyzer.ui.screens.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.screens.books.deleted.RecentlyDeletedRoute
import com.viroge.booksanalyzer.ui.screens.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.screens.books.library.LibraryRoute
import com.viroge.booksanalyzer.ui.screens.settings.SettingsRoute
import com.viroge.booksanalyzer.ui.screens.terms.TermsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
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
                    onOpenBook = { navController.navigate(Routes.BOOK_DETAILS) },
                )
            }

            composable(Routes.BOOK_DETAILS) {
                BookDetailsRoute(
                    onBack = { navController.popBackStack() },
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
                    onGoToConfirm = { navController.navigate(Routes.CONFIRM_BOOK) },
                )
            }

            composable(Routes.CONFIRM_BOOK) {
                ConfirmBookRoute(
                    onBack = { navController.popBackStack() },
                    onBookSaved = {
                        navController.navigate(Routes.BOOK_DETAILS) {
                            popUpTo(Routes.SEARCH_BOOK) { inclusive = false }
                        }
                    },
                )
            }

            composable(Routes.BOOK_DETAILS) {
                BookDetailsRoute(
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
