package com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the View Staff screen.
 */
sealed class ViewStaffEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : ViewStaffEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
    ) : ViewStaffEvent()
}
