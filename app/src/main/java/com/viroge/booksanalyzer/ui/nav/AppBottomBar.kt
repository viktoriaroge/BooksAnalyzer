package com.viroge.booksanalyzer.ui.nav

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomItem(label = "My Books", route = Routes.LIBRARY),
        BottomItem(label = "Find Books", route = Routes.ADD_BOOK_FLOW),
        BottomItem(label = "Profile", route = Routes.PROFILE),
    )

    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                label = { Text(text = item.label) },
                icon = { Text(text = item.label.first().toString()) } // placeholder icon
            )
        }
    }
}

private data class BottomItem(
    val label: String,
    val route: String,
)
