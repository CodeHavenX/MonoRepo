package com.cramsan.edifikana.client.lib.features.debug

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.debug.main.DebugScreen
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorScreen
import com.cramsan.framework.annotations.RouteSafePath

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
                DebugActivityRoute.ScreenSelector -> composable(it.route) {
                    ScreenSelectorScreen()
                }
            }
        }
    }
}
