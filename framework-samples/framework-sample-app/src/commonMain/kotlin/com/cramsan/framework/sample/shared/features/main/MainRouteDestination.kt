@file:Suppress("TooManyFunctions")

package com.cramsan.framework.sample.shared.features.main

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the main activity.
 */
sealed class MainRouteDestination : Destination {

    /**
     * A class representing a main menu.
     */
    @Serializable
    data object MainMenuDestination : MainRouteDestination()

    /**
     * A class representing a halt util.
     */
    @Serializable
    data object HaltUtilDestination : MainRouteDestination()

    /**
     * A class representing a logging destination.
     */
    @Serializable
    data object LoggingDestination : MainRouteDestination()
}
