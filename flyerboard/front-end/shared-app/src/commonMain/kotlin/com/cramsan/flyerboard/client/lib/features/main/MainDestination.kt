@file:Suppress("TooManyFunctions")

package com.cramsan.flyerboard.client.lib.features.main

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
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
     */
    @Serializable
    data class FlyerEditDestination(val flyerId: String) : MainDestination()

    /**
     * Flyer Submit screen destination (create a new flyer).
     */
    @Serializable
    data object FlyerSubmitDestination : MainDestination()

    override fun toWebPath(): String =
        when (this) {
        is FlyerListDestination -> Companion.flyerListRoute.toWebPath(this)
        is FlyerDetailDestination -> Companion.flyerDetailRoute.toWebPath(this)
        is MyFlyersDestination -> Companion.myFlyersRoute.toWebPath(this)
        is ArchiveDestination -> Companion.archiveRoute.toWebPath(this)
        is ModerationQueueDestination -> Companion.moderationQueueRoute.toWebPath(this)
        is FlyerEditDestination -> Companion.flyerEditRoute.toWebPath(this)
        is FlyerSubmitDestination -> Companion.flyerSubmitRoute.toWebPath(this)
    }

    companion object {
        private val flyerListRoute by lazy { webRoute<FlyerListDestination>("/") }
        private val flyerDetailRoute by lazy { webRoute<FlyerDetailDestination>("/flyer") }
        private val myFlyersRoute by lazy { webRoute<MyFlyersDestination>("/my-flyers") }
        private val archiveRoute by lazy { webRoute<ArchiveDestination>("/archive") }
        private val moderationQueueRoute by lazy { webRoute<ModerationQueueDestination>("/moderation") }
        private val flyerEditRoute by lazy { webRoute<FlyerEditDestination>("/my-flyers/edit") }
        private val flyerSubmitRoute by lazy { webRoute<FlyerSubmitDestination>("/submit") }

        /** Parses [path] and returns the matching [MainDestination], or null if unrecognised. */
        fun fromWebPath(path: String): MainDestination? =
            flyerListRoute.fromWebPath(path)
                ?: flyerDetailRoute.fromWebPath(path)
                ?: myFlyersRoute.fromWebPath(path)
                ?: archiveRoute.fromWebPath(path)
                ?: moderationQueueRoute.fromWebPath(path)
                ?: flyerEditRoute.fromWebPath(path)
                ?: flyerSubmitRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<FlyerListDestination>()
                ?: entry.toWebPathIfRoute<FlyerDetailDestination>()
                ?: entry.toWebPathIfRoute<MyFlyersDestination>()
                ?: entry.toWebPathIfRoute<ArchiveDestination>()
                ?: entry.toWebPathIfRoute<ModerationQueueDestination>()
                ?: entry.toWebPathIfRoute<FlyerEditDestination>()
                ?: entry.toWebPathIfRoute<FlyerSubmitDestination>()
    }
}
