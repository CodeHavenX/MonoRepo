package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the AddProperty feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class AddPropertyEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : AddPropertyEvent()
}
