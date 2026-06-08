package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute

/**
 * Base interface for navigation destinations that have a canonical web URL.
 *
 * Declare destinations as plain `data object` or `data class` — no per-destination boilerplate needed.
 * All routing lives in the sealed class companion via [webRoute]. Use an exhaustive `when` in
 * `toWebPath()` so the compiler catches any destination added without a corresponding route:
 *
 * ```
 * @Serializable
 * sealed class MyDestination : WebDestination {
 *
 *     @Serializable
 *     data object FooDestination : MyDestination()
 *
 *     @Serializable
 *     data class BarDestination(val id: String) : MyDestination()
 *
 *     override fun toWebPath(): String = when (this) {
 *         is FooDestination -> Companion.fooRoute.toWebPath(this)
 *         is BarDestination -> Companion.barRoute.toWebPath(this)
 *     }
 *
 *     companion object {
 *         private val fooRoute by lazy { webRoute<FooDestination>("/foo") }
 *         private val barRoute by lazy { webRoute<BarDestination>("/bar") }
 *
 *         fun fromWebPath(path: String): MyDestination? =
 *             fooRoute.fromWebPath(path)
 *                 ?: barRoute.fromWebPath(path)
 *
 *         fun toWebPath(entry: NavBackStackEntry): String? =
 *             entry.toWebPathIfRoute<FooDestination>()
 *                 ?: entry.toWebPathIfRoute<BarDestination>()
 *     }
 * }
 * ```
 */
interface WebDestination : Destination {
    /** Returns the canonical URL string for this destination. */
    fun toWebPath(): String
}

/**
 * Returns [toWebPath] for [T] if this back-stack entry matches the route, or null otherwise.
 */
inline fun <reified T : WebDestination> NavBackStackEntry.toWebPathIfRoute(): String? =
    if (destination.hasRoute<T>()) toRoute<T>().toWebPath() else null
