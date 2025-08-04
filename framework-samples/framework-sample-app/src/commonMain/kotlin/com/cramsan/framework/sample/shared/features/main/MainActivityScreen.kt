package com.cramsan.framework.sample.shared.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.framework.sample.shared.features.ApplicationNavGraphDestination
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilScreen
import com.cramsan.framework.sample.shared.features.main.logging.LoggingScreen
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuScreen

/**
 * Main Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation() {
    navigationGraph(
        graphDestination = ApplicationNavGraphDestination.MainDestination::class,
        startDestination = MainDestination.MainMenuDestination,
        typeMap = emptyMap(),
    ) {
        composable(MainDestination.MainMenuDestination::class) {
            MainMenuScreen()
        }
        composable(MainDestination.HaltUtilDestination::class) {
            HaltUtilScreen()
        }
        composable(MainDestination.LoggingDestination::class) {
            LoggingScreen()
        }
    }
}
