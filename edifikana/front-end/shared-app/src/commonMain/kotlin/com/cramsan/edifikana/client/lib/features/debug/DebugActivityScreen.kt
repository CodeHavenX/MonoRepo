package com.cramsan.edifikana.client.lib.features.debug

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.debug.main.DebugScreen
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph

/**
 * Debug Activity Route.
 */
fun NavGraphBuilder.debugActivityNavigation() {
    navigationGraph(
        graphDestination = ActivityRouteDestination.DebugRouteDestination::class,
        startDestination = DebugRouteDestination.MainDebugDestination,
    ) {
        composable(DebugRouteDestination.MainDebugDestination::class) {
            DebugScreen()
        }
        composable(DebugRouteDestination.ScreenSelectorDestination::class) {
            ScreenSelectorScreen()
        }
    }
}
