package com.viroge.booksanalyzer.ui.nav

import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.viroge.booksanalyzer.domain.model.BookSeed
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

    val bookSeedNavType = object : NavType<BookSeed?>(isNullableAllowed = true) {
        override fun get(bundle: Bundle, key: String): BookSeed? {
            // Modern type-safe way (API 33+)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, BookSeed::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(key)
            }
        }

        override fun parseValue(value: String): BookSeed? {
            // If the value is "null" (as a string from the route), return null
            if (value == "null") return null
            return Json.decodeFromString<BookSeed>(Uri.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: BookSeed?) {
            bundle.putParcelable(key, value)
        }
    }

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
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navigateSafe("${Routes.BOOK_DETAILS}/$seedJson")
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
                            navigateSafe(route = Routes.SEARCH_BOOK_GRAPH, isTabSwitch = true)
                        },
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navigateSafe("${Routes.BOOK_DETAILS}/$seedJson")
                        },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(
                    route = "${Routes.BOOK_DETAILS}/{${Routes.BOOK_SEED_ARG}}",
                    arguments = listOf(
                        navArgument(Routes.BOOK_SEED_ARG) { type = bookSeedNavType }
                    )
                ) {
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
                        onOpenBook = { seed ->
                            val seedJson = Uri.encode(Json.encodeToString(seed))
                            navigateSafe("${Routes.BOOK_DETAILS}/$seedJson") {
                                popUpTo(Routes.SEARCH_BOOK) { inclusive = false }
                            }
                        },
                    )
                }

                composable(
                    route = "${Routes.BOOK_DETAILS}/{${Routes.BOOK_SEED_ARG}}",
                    arguments = listOf(
                        navArgument(Routes.BOOK_SEED_ARG) { type = bookSeedNavType }
                    )
                ) {
                    BookDetailsRoute(
                        onBack = navController::popBackStack,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
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
