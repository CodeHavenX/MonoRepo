package com.cramsan.templatereplaceme.client.lib.navigation

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.Destination

// Each time you run `create activity`, it generates an <Activity>Destination.kt sealed class
// with companion `toWebPath(NavBackStackEntry)` and `fromWebPath(path)` functions. Register the
// new activity here by:
//   1. Adding its `toWebPath` function to [entryToPathHandlers]
//   2. Adding its `fromWebPath` function to [pathToDestinationHandlers]
//
// Example after running `create activity --name Main`:
//   import com.cramsan.templatereplaceme.client.lib.features.main.MainDestination
//
//   private val entryToPathHandlers: List<(NavBackStackEntry) -> String?> =
//       listOf({ entry -> MainDestination.toWebPath(entry) })
//
//   private val pathToDestinationHandlers: List<(String) -> Destination?> =
//       listOf({ path -> MainDestination.fromWebPath(path) })

private val entryToPathHandlers: List<(NavBackStackEntry) -> String?> =
    // TODO: Add one entry per activity as you run `create activity`.
    emptyList()

private val pathToDestinationHandlers: List<(String) -> Destination?> =
    // TODO: Add one entry per activity as you run `create activity`.
    emptyList()

/**
 * Converts the current [NavBackStackEntry] to a canonical URL path by delegating to each
 * registered activity's own path handler. Returns null for destinations that should not update
 * the browser URL (nav-graph containers, splash, and debug screens).
 *
 * Plugged into [BrowserNavigator.attach] as the `destinationToPath` callback:
 * ```
 * browserNavigator.attach(navController, ::entryToPath) { path ->
 *     pathToDestination(path)?.let { navigate { navController.navigate(it) } }
 * }
 * ```
 */
fun entryToPath(entry: NavBackStackEntry): String? =
    entryToPathHandlers.firstNotNullOfOrNull { it(entry) }

/**
 * Converts a canonical URL path into the matching typed [Destination] for this app.
 * Returns null for unrecognised paths; the caller should fall back to the default start destination.
 *
 * Used to resolve the initial deep-link on startup:
 * ```
 * val initialDestination = remember {
 *     browserNavigator.getInitialPath()?.let { pathToDestination(it) }
 * }
 * ```
 */
fun pathToDestination(path: String): Destination? =
    pathToDestinationHandlers.firstNotNullOfOrNull { it(path) }
