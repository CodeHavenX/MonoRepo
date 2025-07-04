@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.edifikana.client.lib.features.window.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the Debug activity.
 */
enum class DebugActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Debug(route = "${ApplicationRoute.Debug.rawRoute}/"),
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
}
