package com.cramsan.edifikana.client.lib.features.debug

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.RouteSafePath
import com.cramsan.edifikana.client.lib.features.debug.main.DebugScreen

/**
 * Debug Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.debugActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = DebugActivityRoute.Debug.route,
    ) {
        DebugActivityRoute.entries.forEach {
            when (it) {
                DebugActivityRoute.Debug -> composable(it.route) {
                    DebugScreen()
                }
            }
        }
    }
}
