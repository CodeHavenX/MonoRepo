package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.Serializable

// TODO: Add `ActivityReplacemeNavGraphDestination` to `TemplateReplaceMeWindowNavGraphDestination`
//       sealed class so the app-level router can navigate to this nav graph.
//       Example:
//           @Serializable
//           data object ActivityReplacemeNavGraphDestination : TemplateReplaceMeWindowNavGraphDestination()

/**
 * All navigation destinations within the [ActivityReplaceme] nav graph.
 *
 * Every direct subclass must carry a [WebPath] annotation with its canonical URL. The KSP
 * processor in `framework:web-route-ksp` reads these annotations to generate
 * `ActivityReplacemeDestinationWebRoutes` (backing [toWebPath]/[fromWebPath]/
 * [toWebPath(NavBackStackEntry)] below), and fails the build if any subclass is missing one.
 *
 * Adding a 2nd+ destination is just: add the `data object`/`data class` with its own
 * `@WebPath("/...")`. No manual route wiring needed — the codegen, and the app-level path
 * aggregator (`<AppName>PathNavigation`, also KSP-generated), pick it up automatically.
 *
 * Examples:
 * ```
 * // Simple destination (no arguments):
 * @Serializable
 * @WebPath("/list")
 * data object ListDestination : ActivityReplacemeDestination()
 *
 * // Destination that carries a typed argument:
 * @Serializable
 * @WebPath("/detail")
 * data class DetailDestination(val id: String) : ActivityReplacemeDestination()
 * ```
 *
 * For destinations with custom types (e.g. value-class IDs), remember to supply
 * a matching `NavType` entry in the `typeMap` passed to [activityReplacemeNavGraphNavigation].
 *
 * TODO: Replace `FeatureReplacemeDestination` with the real destinations for this activity.
 */
@Serializable
sealed class ActivityReplacemeDestination : WebDestination {
    /** Placeholder — replace with the first real screen destination. */
    @Serializable
    @WebPath("/TODO-feature-replaceme")
    data object FeatureReplacemeDestination : ActivityReplacemeDestination()

    override fun toWebPath(): String = ActivityReplacemeDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [ActivityReplacemeDestination], or null if unrecognised. */
        fun fromWebPath(path: String): ActivityReplacemeDestination? =
            ActivityReplacemeDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            ActivityReplacemeDestinationWebRoutes.toWebPath(entry)
    }
}
