package com.cramsan.edifikana.client.lib.navigation

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Converts the current [NavBackStackEntry] to a canonical URL path for Edifikana by delegating
 * to each destination's own [toWebPath]. Returns null for destinations that should not update
 * the browser URL (nav-graph containers, splash, and debug screens).
 */
fun edifikanaEntryToPath(entry: NavBackStackEntry): String? =
    AuthDestination.toWebPath(entry)
        ?: HomeDestination.toWebPath(entry)
        ?: AccountDestination.toWebPath(entry)
        ?: SettingsDestination.toWebPath(entry)

/**
 * Converts a canonical URL path into the matching typed navigation [Destination] for Edifikana.
 * Returns null for unrecognized paths (the caller should fall back to the default start destination).
 */
fun edifikanaPathToDestination(path: String): Destination? =
    AuthDestination.fromWebPath(path)
        ?: HomeDestination.fromWebPath(path)
        ?: AccountDestination.fromWebPath(path)
        ?: SettingsDestination.fromWebPath(path)
