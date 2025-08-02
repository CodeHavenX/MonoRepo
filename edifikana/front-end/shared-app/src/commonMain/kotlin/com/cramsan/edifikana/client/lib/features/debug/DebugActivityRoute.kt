@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.edifikana.client.lib.features.window.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.framework.ammotations.RouteSafePath

/**
 * Routes in the Debug activity.
 */
enum class DebugActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Debug(route = "${ApplicationRoute.Debug.rawRoute}/"),
    ScreenSelector(route = "${ApplicationRoute.Debug.rawRoute}/screen_selector")
}

/**
 * Destinations in the Debug activity.
 */
sealed class DebugRouteDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * An example class representing navigating to a screen within the Debug activity.
     */
    data object MainDebugDestination : DebugRouteDestination(
        DebugActivityRoute.Debug.route,
    )

    /**
     * A class representing navigating to the ScreenSelector screen.
     */
    data object ScreenSelectorDestination : DebugRouteDestination(
        DebugActivityRoute.ScreenSelector.route,
    )
}
