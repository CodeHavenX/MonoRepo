@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Debug nav graph.
 */
@Serializable
sealed class DebugDestination : Destination {

    /**
     * An example class representing navigating to a screen within the Debug nav graph.
     */
    @Serializable
    data object MainDebugDestination : DebugDestination()

    /**
     * A class representing navigating to the ScreenSelector screen.
     */
    @Serializable
    data object ScreenSelectorDestination : DebugDestination()
}
