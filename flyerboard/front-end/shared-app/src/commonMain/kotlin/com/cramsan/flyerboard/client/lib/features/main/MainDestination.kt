@file:Suppress("TooManyFunctions")

package com.cramsan.flyerboard.client.lib.features.main

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the main nav graph.
 */
sealed class MainDestination : Destination {

    /**
     * Flyer list (public feed) screen destination.
     */
    @Serializable
    data object FlyerListDestination : MainDestination()

    /**
     * Dev/debug menu screen destination.
     */
    @Serializable
    data object MenuDestination : MainDestination()
}
