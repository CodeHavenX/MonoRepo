package com.cramsan.framework.sample.shared.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.framework.core.compose.RouteSafePath
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilScreen
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuScreen

/**
 * Main Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.mainActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = MainRoute.MainMenu.route,
    ) {
        MainRoute.entries.forEach {
            when (it) {
                MainRoute.MainMenu -> composable(it.route) {
                    MainMenuScreen()
                }
                MainRoute.HaltUtil -> composable(it.route) {
                    HaltUtilScreen()
                }
            }
        }
    }
}
