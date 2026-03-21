package com.viroge.booksanalyzer.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.viroge.booksanalyzer.R

@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomItem(
            route = Routes.LIBRARY_GRAPH,
            label = stringResource(R.string.library_screen_name),
            icon = Icons.Default.LocalLibrary,
        ),
        BottomItem(
            route = Routes.SEARCH_BOOK_GRAPH,
            label = stringResource(R.string.search_screen_name),
            icon = Icons.Default.Search,
        ),
        BottomItem(
            route = Routes.SETTINGS_GRAPH,
            label = stringResource(R.string.settings_screen_name),
            icon = Icons.Default.Settings,
        ),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        items.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)
