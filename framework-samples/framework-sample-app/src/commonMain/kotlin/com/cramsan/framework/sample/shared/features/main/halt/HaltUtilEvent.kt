package com.cramsan.framework.sample.shared.features.main.halt

import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.sample.shared.features.ApplicationEvent

/**
 * Events that can be triggered within the domain of the HaltUtil feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class HaltUtilEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : HaltUtilEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        // Update this with the respective ApplicationEvent type.
        val applicationEvent: ApplicationEvent,
    ) : HaltUtilEvent()
}
