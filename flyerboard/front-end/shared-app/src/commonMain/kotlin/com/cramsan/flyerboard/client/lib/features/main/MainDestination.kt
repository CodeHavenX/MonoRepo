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
     * Flyer detail screen destination. [flyerId] is the raw string value of [FlyerId].
     */
    @Serializable
    data class FlyerDetailDestination(val flyerId: String) : MainDestination()

    /**
     * My Flyers screen destination (authenticated user's own flyers).
     */
    @Serializable
    data object MyFlyersDestination : MainDestination()

    /**
     * Archive screen destination (publicly browsable expired/archived flyers).
     */
    @Serializable
    data object ArchiveDestination : MainDestination()

    /**
     * Moderation Queue screen destination (admin-only pending flyer review).
     */
    @Serializable
    data object ModerationQueueDestination : MainDestination()

    /**
     * Flyer Edit screen destination. [flyerId] is the raw string value of [FlyerId].
     * Route registered in FlyerUpload/FlyerEdit feature (TASK-022).
     */
    @Serializable
    data class FlyerEditDestination(val flyerId: String) : MainDestination()

    /**
     * Dev/debug menu screen destination.
     */
    @Serializable
    data object MenuDestination : MainDestination()
}
