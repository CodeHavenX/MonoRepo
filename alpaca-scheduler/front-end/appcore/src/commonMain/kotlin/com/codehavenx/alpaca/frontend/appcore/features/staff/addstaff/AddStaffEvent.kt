package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Add Staff screen.
 */
sealed class AddStaffEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : AddStaffEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : AddStaffEvent()
}
