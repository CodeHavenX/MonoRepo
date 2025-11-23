package com.cramsan.edifikana.client.lib.features.settings

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Settings nav graph.
 */
@Serializable
sealed class SettingsDestination : Destination {
    /**
     * Destination for the Settings overview screen.
     */
    @Serializable
    data object SettingsOverviewDestination : SettingsDestination()
}
