@file:Suppress("TooManyFunctions")

package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

// TODO: Add `ActivityReplacemeNavGraphDestination` to `TemplateReplaceMeWindowNavGraphDestination`
//       sealed class so the app-level router can navigate to this nav graph.
//       Example:
//           @Serializable
//           data object ActivityReplacemeNavGraphDestination : TemplateReplaceMeWindowNavGraphDestination()

/**
 * All navigation destinations within the [ActivityReplaceme] nav graph.
 *
 * Add one `@Serializable` sealed subclass per screen in this activity. The destination
 * types are referenced both here and in [ActivityReplacemeActivityScreen] where composables
 * are registered.
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
 * a matching `NavType` entry in the `typeMap` passed to
 * [activityreplacemeNavGraphNavigation].
 *
 * TODO: Replace `FeatureReplacemeDestination` with the real destinations for this activity.
 */
sealed class ActivityReplacemeDestination : Destination {
    /** Placeholder — replace with the first real screen destination. */
    @Serializable
    data object FeatureReplacemeDestination : ActivityReplacemeDestination()
}
