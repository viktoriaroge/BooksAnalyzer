package com.viroge.booksanalyzer.ui.nav

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import com.viroge.booksanalyzer.domain.model.BookSeed
import com.viroge.booksanalyzer.domain.model.TempBook
import kotlinx.serialization.json.Json

val bookSeedNavType = parcelableNavType<BookSeed>()
val tempBookSeedNavType = parcelableNavType<TempBook>()

/**
 * Extension to create a generic Parcelable NavType to avoid code duplication.
 */
inline fun <reified T : Parcelable> parcelableNavType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T? =
        if (value == "null") null else json.decodeFromString<T>(Uri.decode(value))

    override fun put(bundle: Bundle, key: String, value: T?) =
        bundle.putParcelable(key, value)
}

fun NavHostController.navigateSafe(
    route: String,
    isTabSwitch: Boolean = false,
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null,
) {
    val isAlreadyThere = currentDestination?.hierarchy?.any { it.route == route } == true

    if (!isAlreadyThere) {
        navigate(route) {
            if (isTabSwitch) {
                // Standard Tab Switching logic
                popUpTo(graph.findStartDestination().id) {
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
