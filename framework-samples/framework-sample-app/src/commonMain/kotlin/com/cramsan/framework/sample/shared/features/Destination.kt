package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Destination representing a single screen or composable within the navigation graph.
 */
interface Destination {
    @RouteSafePath
    val path: String
}
