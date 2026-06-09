@file:Suppress("TooManyFunctions")

package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
import kotlinx.serialization.Serializable

// TODO: Add `ActivityReplacemeNavGraphDestination` to `TemplateReplaceMeWindowNavGraphDestination`
//       sealed class so the app-level router can navigate to this nav graph.
//       Example:
//           @Serializable
//           data object ActivityReplacemeNavGraphDestination : TemplateReplaceMeWindowNavGraphDestination()

/**
 * All navigation destinations within the [ActivityReplaceme] nav graph.
 *
 * Each subclass implements [WebDestination], which requires [toWebPath]. The exhaustive
 * `when(this)` in the body means the compiler rejects any new subclass that lacks a branch —
 * ensuring every screen gets a URL automatically when added.
 *
 * Routes are defined as `by lazy` companion vals to avoid circular JVM class initialisation
 * with `@Serializable data object`. [fromWebPath] and [toWebPath(NavBackStackEntry)] are the
 * two functions plugged directly into [BrowserNavigator] from the app's `PathNavigation.kt`.
 *
 * Examples:
 * ```
 * // Simple destination (no arguments):
 * @Serializable data object ListDestination : ActivityReplacemeDestination()
 *
 * // Destination that carries a typed argument:
 * @Serializable data class DetailDestination(val id: String) : ActivityReplacemeDestination()
 * ```
 *
 * For destinations with custom types (e.g. value-class IDs), remember to supply
 * a matching `NavType` entry in the `typeMap` passed to [activityReplacemeNavGraphNavigation].
 *
 * Adding a 2nd+ destination means: add the `data object`/`data class`, add a `by lazy` route val,
 * add a branch to [toWebPath], and chain it onto [fromWebPath] / [toWebPath(NavBackStackEntry)]
 * with `?:`:
 * ```
 * override fun toWebPath(): String = when (this) {
 *     is FeatureReplacemeDestination -> Companion.featureReplacemeRoute.toWebPath(this)
 *     is DetailsDestination -> Companion.detailsRoute.toWebPath(this)
 * }
 *
 * companion object {
 *     private val featureReplacemeRoute by lazy { webRoute<FeatureReplacemeDestination>("/TODO-feature-replaceme") }
 *     private val detailsRoute by lazy { webRoute<DetailsDestination>("/details") }
 *
 *     fun fromWebPath(path: String): ActivityReplacemeDestination? =
 *         featureReplacemeRoute.fromWebPath(path) ?: detailsRoute.fromWebPath(path)
 *
 *     fun toWebPath(entry: NavBackStackEntry): String? =
 *         entry.toWebPathIfRoute<FeatureReplacemeDestination>() ?: entry.toWebPathIfRoute<DetailsDestination>()
 * }
 * ```
 *
 * TODO: Replace `FeatureReplacemeDestination` with the real destinations for this activity.
 */
@Serializable
sealed class ActivityReplacemeDestination : WebDestination {
    /** Placeholder — replace with the first real screen destination. */
    @Serializable
    data object FeatureReplacemeDestination : ActivityReplacemeDestination()

    override fun toWebPath(): String = when (this) {
        // TODO: Replace the route path with your real URL segment.
        is FeatureReplacemeDestination -> Companion.featureReplacemeRoute.toWebPath(this)
    }

    companion object {
        // TODO: Replace "/TODO-feature-replaceme" with the real URL path for this screen.
        private val featureReplacemeRoute by lazy { webRoute<FeatureReplacemeDestination>("/TODO-feature-replaceme") }

        /** Parses [path] and returns the matching [ActivityReplacemeDestination], or null if unrecognised. */
        fun fromWebPath(path: String): ActivityReplacemeDestination? =
            featureReplacemeRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<FeatureReplacemeDestination>()
    }
}
