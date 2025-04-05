package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Property feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : PropertyEvent()
}
