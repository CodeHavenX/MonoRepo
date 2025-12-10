package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the PropertiesOverview feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertiesOverviewEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : PropertiesOverviewEvent()
}
