package com.viroge.booksanalyzer.ui.snackbar

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppSnackbar = staticCompositionLocalOf<AppSnackbarController> {
    error("LocalAppSnackbar not provided")
}
