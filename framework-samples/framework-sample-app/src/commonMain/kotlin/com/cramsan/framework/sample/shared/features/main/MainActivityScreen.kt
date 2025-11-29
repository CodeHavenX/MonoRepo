package com.cramsan.framework.sample.shared.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.framework.sample.shared.features.ApplicationNavGraphDestination
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilScreen
import com.cramsan.framework.sample.shared.features.main.logging.LoggingScreen
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuScreen
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Main Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = ApplicationNavGraphDestination.MainDestination::class,
        startDestination = MainDestination.MainMenuDestination,
        typeMap = typeMap,
    ) {
        composable(
            MainDestination.MainMenuDestination::class,
            typeMap = typeMap,
        ) {
            MainMenuScreen()
        }
        composable(
            MainDestination.HaltUtilDestination::class,
            typeMap = typeMap,
        ) {
            HaltUtilScreen()
        }
        composable(
            MainDestination.LoggingDestination::class,
            typeMap = typeMap,
        ) {
            LoggingScreen()
        }
    }
}
