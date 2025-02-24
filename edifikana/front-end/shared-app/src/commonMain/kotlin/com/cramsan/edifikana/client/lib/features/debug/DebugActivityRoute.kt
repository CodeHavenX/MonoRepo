@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.edifikana.client.lib.features.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the Debug activity.
 */
enum class DebugActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Debug(route = "${ApplicationRoute.Debug.route}/"),
    ;
}

/**
 * Destinations in the Debug activity.
 */
sealed class DebugRouteDestination(
    @RouteSafePath
    override val path: String,
) : Destination {

    /**
     * An example class representing navigating to a screen within the Debug activity.
     */
    data object MainDebugDestination : DebugRouteDestination(
        DebugActivityRoute.Debug.route,
    )
}
