package com.cramsan.framework.sample.shared.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.framework.sample.shared.features.ActivityDestination
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilScreen
import com.cramsan.framework.sample.shared.features.main.logging.LoggingScreen
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuScreen

/**
 * Main Activity Route.
 */
fun NavGraphBuilder.mainActivityNavigation() {
    navigationGraph(
        graphDestination = ActivityDestination.MainDestination::class,
        startDestination = MainRouteDestination.MainMenuDestination,
    ) {
        composable(MainRouteDestination.MainMenuDestination::class) {
            MainMenuScreen()
        }
        composable(MainRouteDestination.HaltUtilDestination::class) {
            HaltUtilScreen()
        }
        composable(MainRouteDestination.LoggingDestination::class) {
            LoggingScreen()
        }
    }
}
