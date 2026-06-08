@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.debug

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Debug nav graph.
 *
 * Implements [Destination] rather than [WebDestination] because debug screens intentionally
 * return null from [toWebPath] to opt out of browser URL syncing. [WebDestination.toWebPath]
 * is non-nullable and cannot represent that intent.
 */
@Serializable
sealed class DebugDestination : Destination {
    /**
     * Returns the canonical browser URL path for this destination, or null if this destination
     * should not be reflected in the browser address bar.
     */
    abstract fun toWebPath(): String?

    /**
     * An example class representing navigating to a screen within the Debug nav graph.
     */
    @Serializable
    data object MainDebugDestination : DebugDestination() {
        override fun toWebPath() = null
    }

    /**
     * A class representing navigating to the ScreenSelector screen.
     */
    @Serializable
    data object ScreenSelectorDestination : DebugDestination() {
        override fun toWebPath() = null
    }
}
