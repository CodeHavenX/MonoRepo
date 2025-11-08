@file:Suppress("TooManyFunctions")

package com.cramsan.templatereplaceme.client.lib.features.main

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth nav graph.
 */
sealed class MainDestination : Destination {

    /**
     * A class representing navigating to the sign in screen.
     */
    @Serializable
    data object MenuDestination : MainDestination()
}
