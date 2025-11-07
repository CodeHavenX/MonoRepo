package com.cramsan.edifikana.client.lib.features.home.properties

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the PropertyManager feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyManagerEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : PropertyManagerEvent()
}
