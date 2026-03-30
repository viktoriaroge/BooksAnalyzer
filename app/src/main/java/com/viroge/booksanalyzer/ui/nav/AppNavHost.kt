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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.nav.Routes.APP_TERMS_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.BOOK_DETAILS_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.BOOK_DETAILS_ROUTE_PREFIX
import com.viroge.booksanalyzer.ui.nav.Routes.COLLECTION_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.COLLECTION_ROUTE_PREFIX
import com.viroge.booksanalyzer.ui.nav.Routes.CONFIRM_BOOK_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.CONFIRM_BOOK_ROUTE_PREFIX
import com.viroge.booksanalyzer.ui.nav.Routes.LIBRARY_GRAPH
import com.viroge.booksanalyzer.ui.nav.Routes.LIBRARY_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.LIBRARY_ROUTE_PREFIX
import com.viroge.booksanalyzer.ui.nav.Routes.RECENTLY_DELETED_BOOKS_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.SEARCH_BOOK_GRAPH
import com.viroge.booksanalyzer.ui.nav.Routes.SEARCH_BOOK_ROUTE
import com.viroge.booksanalyzer.ui.nav.Routes.SEARCH_BOOK_ROUTE_PREFIX
import com.viroge.booksanalyzer.ui.nav.Routes.SETTINGS_GRAPH
import com.viroge.booksanalyzer.ui.nav.Routes.SETTINGS_ROUTE
import com.viroge.booksanalyzer.ui.nav.StateArguments.BOOK_SEED_ARG
import com.viroge.booksanalyzer.ui.nav.StateArguments.TRANSITION_PREFIX_ARG
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
    val libraryTransitionPrefix = TransitionNamespace.Library.prefix
    val collectionTransitionPrefix = TransitionNamespace.Collection.prefix
    val searchTransitionPrefix = TransitionNamespace.Search.prefix

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = LIBRARY_GRAPH,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
        ) {

            // --- LIBRARY TAB --------------------------------------------------------------

            navigation(
                route = LIBRARY_GRAPH,
                startDestination = "$LIBRARY_ROUTE_PREFIX/$libraryTransitionPrefix",
            ) {
                composable(
                    route = LIBRARY_ROUTE,
                    arguments = listOf(
                        navArgument(TRANSITION_PREFIX_ARG) { type = NavType.StringType },
                    ),
                ) {
                    LibraryRoute(
                        // Animation Parameters:
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,

                        onOpenSearch = { navController.navigateSafe(route = SEARCH_BOOK_GRAPH, isTabSwitch = true) },
                        onOpenCollection = { navController.navigateSafe(route = "$COLLECTION_ROUTE_PREFIX/$collectionTransitionPrefix") },
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe(route = "${BOOK_DETAILS_ROUTE_PREFIX}/$seedJson/$libraryTransitionPrefix")
                        },
                    )
                }

                composable(
                    route = COLLECTION_ROUTE,
                    arguments = listOf(
                        navArgument(TRANSITION_PREFIX_ARG) { type = NavType.StringType },
                    ),
                ) {
                    CollectionRoute(
                        // Animation Parameters:
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,

                        onBack = navController::popBackStack,
                        onOpenSearch = {
                            navController.popBackStack(route = LIBRARY_ROUTE, inclusive = false)
                            navController.navigateSafe(route = SEARCH_BOOK_GRAPH, isTabSwitch = true)
                        },
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${BOOK_DETAILS_ROUTE_PREFIX}/$seedJson/$collectionTransitionPrefix")
                        },
                    )
                }

                bookDetailsDestination(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBack = navController::popBackStack,
                )
            }

            // --- SEARCH BOOK TAB -------------------------------------------------------------

            navigation(
                route = SEARCH_BOOK_GRAPH,
                startDestination = "$SEARCH_BOOK_ROUTE_PREFIX/$searchTransitionPrefix",
            ) {
                composable(
                    route = SEARCH_BOOK_ROUTE,
                    arguments = listOf(
                        navArgument(TRANSITION_PREFIX_ARG) { type = NavType.StringType },
                    ),
                ) {
                    SearchBookRoute(
                        // Animation Parameters:
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,

                        onOpenBookConfirmation = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${CONFIRM_BOOK_ROUTE_PREFIX}/$seedJson/$searchTransitionPrefix")
                        },
                    )
                }

                composable(
                    route = CONFIRM_BOOK_ROUTE,
                    arguments = listOf(
                        navArgument(BOOK_SEED_ARG) { type = tempBookSeedNavType },
                        navArgument(TRANSITION_PREFIX_ARG) { type = NavType.StringType },
                    ),
                ) {
                    ConfirmBookRoute(
                        // Animation Parameters:
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,

                        onBack = navController::popBackStack,
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navController.navigateSafe("${BOOK_DETAILS_ROUTE_PREFIX}/$seedJson/$searchTransitionPrefix") {
                                popUpTo(SEARCH_BOOK_ROUTE) { inclusive = false }
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
                route = SETTINGS_GRAPH,
                startDestination = SETTINGS_ROUTE,
            ) {
                composable(SETTINGS_ROUTE) {
                    SettingsRoute(
                        onOpenEntry = navController::navigate,
                    )
                }

                composable(RECENTLY_DELETED_BOOKS_ROUTE) {
                    RecentlyDeletedRoute(
                        onBack = navController::popBackStack,
                    )
                }

                composable(APP_TERMS_ROUTE) {
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
        route = BOOK_DETAILS_ROUTE,
        arguments = listOf(
            navArgument(BOOK_SEED_ARG) { type = bookSeedNavType },
            navArgument(TRANSITION_PREFIX_ARG) { type = NavType.StringType },
        ),
    ) {
        BookDetailsRoute(
            // Animation Parameters:
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,

            onBack = onBack,
        )
    }
}

enum class TransitionNamespace(val prefix: String) {
    Search("search"),
    Library("library"),
    Collection("collection"),
}
