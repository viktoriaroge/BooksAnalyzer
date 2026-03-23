package com.viroge.booksanalyzer.ui.nav

import android.net.Uri
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.screens.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.screens.books.deleted.RecentlyDeletedRoute
import com.viroge.booksanalyzer.ui.screens.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.screens.books.library.LibraryRoute
import com.viroge.booksanalyzer.ui.screens.books.library.collection.CollectionRoute
import com.viroge.booksanalyzer.ui.screens.books.search.SearchBookRoute
import com.viroge.booksanalyzer.ui.screens.settings.SettingsRoute
import com.viroge.booksanalyzer.ui.screens.terms.TermsRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    navController: NavHostController,
) {


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
                        onOpenSearch = { navController.navigateSafe(route = Routes.SEARCH_BOOK_GRAPH, isTabSwitch = true) },
                        onOpenCollection = { navController.navigateSafe(Routes.COLLECTION) },
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${Routes.BOOK_DETAILS}/$seedJson")
                        },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(Routes.COLLECTION) {
                    CollectionRoute(
                        onBack = navController::popBackStack,
                        onOpenSearch = {
                            navController.popBackStack(route = Routes.LIBRARY, inclusive = false)
                            navController.navigateSafe(route = Routes.SEARCH_BOOK_GRAPH, isTabSwitch = true)
                        },
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${Routes.BOOK_DETAILS}/$seedJson")
                        },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                bookDetailsDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBack = navController::popBackStack
                )
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
                        onOpenBookConfirmation = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${Routes.CONFIRM_BOOK}/$seedJson")
                        },
                    )
                }

                composable(
                    route = "${Routes.CONFIRM_BOOK}/{${Routes.TEMP_BOOK_SEED_ARG}}",
                    arguments = listOf(
                        navArgument(Routes.TEMP_BOOK_SEED_ARG) { type = tempBookSeedNavType }
                    )
                ) {
                    ConfirmBookRoute(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                        onBack = navController::popBackStack,
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${Routes.BOOK_DETAILS}/$seedJson") {
                                popUpTo(Routes.SEARCH_BOOK) { inclusive = false }
                            }
                        },
                    )
                }

                bookDetailsDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBack = navController::popBackStack
                )
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

fun NavGraphBuilder.bookDetailsDestination(
    sharedTransitionScope: SharedTransitionScope,
    onBack: () -> Unit,
) {
    composable(
        route = "${Routes.BOOK_DETAILS}/{${Routes.BOOK_SEED_ARG}}",
        arguments = listOf(
            navArgument(Routes.BOOK_SEED_ARG) { type = bookSeedNavType }
        )
    ) {
        BookDetailsRoute(
            onBack = onBack,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
        )
    }
}
