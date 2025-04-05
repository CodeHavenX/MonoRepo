package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Add Staff screen.
 */
sealed class AddStaffEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : AddStaffEvent()
}
