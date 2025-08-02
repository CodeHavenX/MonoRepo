package com.cramsan.edifikana.client.lib.features.window

import com.cramsan.framework.annotations.RouteSafePath

/**
 * Destination representing a single screen or composable within the navigation graph.
 */
interface Destination {
    @RouteSafePath
    val rawRoute: String
}
