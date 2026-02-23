package com.viroge.booksanalyzer.ui.components.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppSnackbarController(
    private val scope: CoroutineScope,
    private val hostState: SnackbarHostState,
) {

    fun show(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = true,
        duration: SnackbarDuration = SnackbarDuration.Long,
        onActionPerformed: (() -> Unit)? = null,
        onDismissed: (() -> Unit)? = null,
    ) {

        scope.launch {
            val result = hostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onActionPerformed?.invoke()
                SnackbarResult.Dismissed -> onDismissed?.invoke()
            }
        }
    }
}