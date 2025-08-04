package com.cramsan.edifikana.client.lib.features.debug

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.debug.main.DebugScreen
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Debug Nav Graph Route.
 */
fun NavGraphBuilder.debugNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.DebugNavGraphDestination::class,
        startDestination = DebugDestination.MainDebugDestination,
        typeMap = typeMap,
    ) {
        composable(DebugDestination.MainDebugDestination::class) {
            DebugScreen()
        }
        composable(DebugDestination.ScreenSelectorDestination::class) {
            ScreenSelectorScreen()
        }
    }
}
