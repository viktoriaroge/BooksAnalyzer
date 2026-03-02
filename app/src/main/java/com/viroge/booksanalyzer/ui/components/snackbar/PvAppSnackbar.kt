package com.viroge.booksanalyzer.ui.components.snackbar

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppSnackbar = staticCompositionLocalOf<PvAppSnackbarController> {
    error("LocalAppSnackbar not provided")
}
