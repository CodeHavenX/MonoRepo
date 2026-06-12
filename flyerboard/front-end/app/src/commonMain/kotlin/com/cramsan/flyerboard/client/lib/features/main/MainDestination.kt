package com.cramsan.flyerboard.client.lib.features.main

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.Serializable

/**
 * Destinations in the main nav graph.
 */
@Serializable
sealed class MainDestination : WebDestination {
    /**
     * Flyer list (public feed) screen destination.
     */
    @Serializable
    @WebPath("/")
    data object FlyerListDestination : MainDestination()

    /**
     * Flyer detail screen destination. [flyerId] is the raw string value of [FlyerId].
     */
    @Serializable
    @WebPath("/flyer")
    data class FlyerDetailDestination(val flyerId: String) : MainDestination()

    /**
     * My Flyers screen destination (authenticated user's own flyers).
     */
    @Serializable
    @WebPath("/my-flyers")
    data object MyFlyersDestination : MainDestination()

    /**
     * Archive screen destination (publicly browsable expired/archived flyers).
     */
    @Serializable
    @WebPath("/archive")
    data object ArchiveDestination : MainDestination()

    /**
     * Moderation Queue screen destination (admin-only pending flyer review).
     */
    @Serializable
    @WebPath("/moderation")
    data object ModerationQueueDestination : MainDestination()

    /**
     * Flyer Edit screen destination. [flyerId] is the raw string value of [FlyerId].
     */
    @Serializable
    @WebPath("/my-flyers/edit")
    data class FlyerEditDestination(val flyerId: String) : MainDestination()

    /**
     * Flyer Submit screen destination (create a new flyer).
     */
    @Serializable
    @WebPath("/submit")
    data object FlyerSubmitDestination : MainDestination()

    override fun toWebPath(): String = MainDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [MainDestination], or null if unrecognised. */
        fun fromWebPath(path: String): MainDestination? = MainDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = MainDestinationWebRoutes.toWebPath(entry)
    }
}
