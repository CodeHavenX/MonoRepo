package com.cramsan.edifikana.client.lib.features.admin.properties

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the PropertyManager feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyManagerEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : PropertyManagerEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
    ) : PropertyManagerEvent()
}
