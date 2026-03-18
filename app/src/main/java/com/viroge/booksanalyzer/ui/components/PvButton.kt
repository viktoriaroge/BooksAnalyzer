package com.viroge.booksanalyzer.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PvButton(
    modifier: Modifier,
    text: String,
    buttonType: PvButtonType = PvButtonType.Primary,
    enabled: Boolean = true,
    onClick: () -> Unit,
) = Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .height(52.dp),
    shape = RoundedCornerShape(16.dp),
    colors = when (buttonType) {
        PvButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        )

        PvButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary, // this one looks better
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        )

        PvButtonType.Error -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    },
) {
    Icon(Icons.Default.Navigation, null)
    Spacer(Modifier.width(8.dp))
    Text(text)
}

enum class PvButtonType {
    Primary, Secondary, Error
}
