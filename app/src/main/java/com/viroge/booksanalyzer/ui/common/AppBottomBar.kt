package com.viroge.booksanalyzer.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.viroge.booksanalyzer.ui.nav.Routes

@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomItem(
            route = Routes.LIBRARY,
            label = "My Books",
            icon = Icons.Default.LocalLibrary,
        ),
        BottomItem(
            route = Routes.ADD_BOOK,
            label = "Find Books",
            icon = Icons.Default.Search,
        ),
        BottomItem(
            route = Routes.PROFILE,
            label = "Profile",
            icon = Icons.Default.AccountCircle,
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
                label = { Text(text = item.label) },
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
