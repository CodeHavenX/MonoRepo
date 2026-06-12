package com.cramsan.flyerboard.client.lib.navigation

import androidx.navigation.NavBackStackEntry
import com.cramsan.flyerboard.client.lib.features.auth.AuthDestination
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Converts a canonical URL path into the matching typed navigation [Destination].
 * This is the authoritative path → destination mapping used when the app launches
 * via a direct URL or a browser back/forward event.
 */
fun pathToDestination(path: String): Destination =
    MainDestination.fromWebPath(path)
        ?: AuthDestination.fromWebPath(path)
        ?: MainDestination.FlyerListDestination

/**
 * Converts the current [NavBackStackEntry] to a canonical URL path for FlyerBoard by delegating
 * to each destination's own [toWebPath]. Returns null for destinations that should not update
 * the browser URL (nav-graph containers or the splash screen).
 */
fun flyerBoardEntryToPath(entry: NavBackStackEntry): String? =
    MainDestination.toWebPath(entry)
        ?: AuthDestination.toWebPath(entry)
