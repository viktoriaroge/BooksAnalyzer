package com.viroge.booksanalyzer.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.viroge.booksanalyzer.ui.books.add.AddBookRoute
import com.viroge.booksanalyzer.ui.books.confirm.ConfirmBookRoute
import com.viroge.booksanalyzer.ui.books.details.BookDetailsRoute
import com.viroge.booksanalyzer.ui.books.library.LibraryRoute
import com.viroge.booksanalyzer.ui.profile.ProfileRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIBRARY,
    ) {

        composable(Routes.LIBRARY) {
            LibraryRoute(
                onOpenBook = { bookId ->
                    navController.navigate(route = "${Routes.BOOK_DETAILS}/$bookId")
                },
            )
        }

        composable(Routes.ADD_BOOK) {
            AddBookRoute(
                onGoToConfirm = { navController.navigate(Routes.CONFIRM_BOOK) },
            )
        }

        composable(Routes.CONFIRM_BOOK) { entry ->
            ConfirmBookRoute(
                navController = navController,
                entry = entry,
                onBack = { navController.popBackStack() },
                onBookSaved = { newBookId ->
                    navController.navigate(route = "${Routes.BOOK_DETAILS}/$newBookId") {
                        popUpTo(Routes.ADD_BOOK) { inclusive = false }
                    }
                },
            )
        }

        composable(
            route = "${Routes.BOOK_DETAILS}/{${Routes.ARG_BOOK_ID}}",
            arguments = listOf(navArgument(name = Routes.ARG_BOOK_ID) {
                type = NavType.StringType
            }),
        ) {
            BookDetailsRoute(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.PROFILE) {
            ProfileRoute()
        }
    }
}
