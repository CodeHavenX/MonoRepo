@file:Suppress("TooManyFunctions")

package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

// TODO: Add ActivityReplacemeNavGraphDestination to TemplateReplaceMeWindowNavGraphDestination sealed class.

/**
 * Destinations within the [ActivityReplaceme] navigation graph.
 *
 * Add a sealed subclass for each screen that belongs to this nav graph.
 */
sealed class ActivityReplacemeDestination : Destination {
    /**
     * Navigates to the [FeatureReplacemeScreen].
     */
    @Serializable
    data object FeatureReplacemeDestination : ActivityReplacemeDestination()
}
