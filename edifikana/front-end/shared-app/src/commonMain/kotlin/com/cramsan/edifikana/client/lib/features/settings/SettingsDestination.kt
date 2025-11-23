package com.cramsan.edifikana.client.lib.features.settings

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the Settings graph.
 */
sealed class SettingsDestination : Destination {
    @Serializable
    data object GeneralSettingsDestination : SettingsDestination()
}
