package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Update Staff screen.
 */
sealed class UpdateStaffEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : UpdateStaffEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : UpdateStaffEvent()
}
