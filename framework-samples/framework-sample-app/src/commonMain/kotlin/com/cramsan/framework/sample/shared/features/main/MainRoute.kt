@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.framework.sample.shared.features.main

import com.cramsan.framework.core.compose.RouteSafePath
import com.cramsan.framework.sample.shared.features.Destination

/**
 * Routes in the main activity.
 */
enum class MainRoute(
    @RouteSafePath
    val route: String,
) {
    MainMenu(route = "main_menu"),
    HaltUtil(route = "halt_util"),
    ;
}

/**
 * Destinations in the main activity.
 */
sealed class MainRouteDestination(
    @RouteSafePath
    override val path: String,
) : Destination {

    /**
     * A class representing a main menu.
     */
    data object MainMenuDestination : MainRouteDestination(
        MainRoute.MainMenu.route,
    )

    /**
     * A class representing a halt util.
     */
    data object HaltUtilDestination : MainRouteDestination(
        MainRoute.HaltUtil.route,
    )
}
