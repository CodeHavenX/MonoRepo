package com.cramsan.edifikana.client.lib.features.debug

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.debug.main.DebugScreen
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Debug Activity Route.
 */
fun NavGraphBuilder.debugActivityNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = ActivityRouteDestination.DebugRouteDestination::class,
        startDestination = DebugRouteDestination.MainDebugDestination,
        typeMap = typeMap,
    ) {
        composable(DebugRouteDestination.MainDebugDestination::class) {
            DebugScreen()
        }
        composable(DebugRouteDestination.ScreenSelectorDestination::class) {
            ScreenSelectorScreen()
        }
    }
}
