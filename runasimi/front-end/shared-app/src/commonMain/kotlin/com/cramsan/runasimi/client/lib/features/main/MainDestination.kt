package com.cramsan.runasimi.client.lib.features.main

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Main graph.
 */
sealed class MainDestination : Destination {
    /**
     * A class representing navigating to the ${Feature_Name} screen.
     */
    @Serializable
    data object NavDestination : MainDestination()

    /**
     * A class representing navigating to the Menu screen.
     */
    @Serializable
    data object MenuDestination : MainDestination()
}
