package com.viroge.booksanalyzer.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.viroge.booksanalyzer.ui.books.add.AddBookRoute
import com.viroge.booksanalyzer.ui.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.books.library.LibraryRoute

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIBRARY,
    ) {

        composable(Routes.LIBRARY) {

            LibraryRoute(
                onAddBook = { navController.navigate(Routes.ADD_BOOK) },
                onOpenBook = { bookId ->
                    navController.navigate("${Routes.BOOK_DETAILS}/$bookId")
                },
            )
        }

        navigation(
            startDestination = Routes.ADD_BOOK,
            route = Routes.ADD_BOOK_FLOW
        ) {

            composable(Routes.ADD_BOOK) { entry ->

                AddBookRoute(
                    navController = navController,
                    entry = entry,
                    onBack = { navController.popBackStack() },
                    onGoToConfirm = { navController.navigate(Routes.CONFIRM_BOOK) },
                )
            }

            composable(Routes.CONFIRM_BOOK) { entry ->

                ConfirmBookRoute(
                    navController = navController,
                    entry = entry,
                    onBack = { navController.popBackStack() },
                    onBookSaved = { newBookId ->
                        navController.navigate("${Routes.BOOK_DETAILS}/$newBookId") {
                            popUpTo(Routes.LIBRARY) { inclusive = false }
                        }
                    },
                )
            }
        }

        composable(
            route = "${Routes.BOOK_DETAILS}/{${Routes.ARG_BOOK_ID}}",
            arguments = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.StringType }),
        ) { entry ->

            val bookId = entry.arguments?.getString(Routes.ARG_BOOK_ID)!! // TODO: handle better
            BookDetailsRoute(
                bookId = bookId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
