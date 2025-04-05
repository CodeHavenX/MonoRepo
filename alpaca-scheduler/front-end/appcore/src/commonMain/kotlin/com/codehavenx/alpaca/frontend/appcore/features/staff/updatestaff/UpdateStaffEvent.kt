package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the Update Staff screen.
 */
sealed class UpdateStaffEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : UpdateStaffEvent()
}
