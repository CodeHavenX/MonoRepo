package com.cramsan.edifikana.client.lib.features.admin.addproperty

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the AddProperty feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class AddPropertyEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : AddPropertyEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
    ) : AddPropertyEvent()
}
