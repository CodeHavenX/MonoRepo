package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the List Staff screen.
 */
sealed class ListStaffsEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ListStaffsEvent()
}
