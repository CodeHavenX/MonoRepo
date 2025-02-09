package com.cramsan.edifikana.client.lib.features

/**
 * Destination representing a single screen or composable within the navigation graph.
 */
interface Destination {
    @RouteSafePath
    val path: String
}
