@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Debug activity.
 */
@Serializable
sealed class DebugRouteDestination : Destination {

    /**
     * An example class representing navigating to a screen within the Debug activity.
     */
    @Serializable
    data object MainDebugDestination : DebugRouteDestination()

    /**
     * A class representing navigating to the ScreenSelector screen.
     */
    @Serializable
    data object ScreenSelectorDestination : DebugRouteDestination()
}
