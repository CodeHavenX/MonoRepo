@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.root.debug

import com.cramsan.edifikana.client.lib.features.root.RouteSafePath

/**
 * Routes in the Debug activity.
 */
enum class DebugActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Debug(route = "debug"),
    ;
}

/**
 * Destinations in the Debug activity.
 */
sealed class DebugRouteDestination(
    @RouteSafePath
    val path: String,
) {

    /**
     * An example class representing navigating to a screen within the Debug activity.
     */
    data object MainDebugDestination : DebugRouteDestination(
        DebugActivityRoute.Debug.route,
    )
}
