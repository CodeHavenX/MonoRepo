package com.cramsan.edifikana.client.lib.features.management.addprimarystaff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the AddPrimaryStaff feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class AddPrimaryStaffEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : AddPrimaryStaffEvent()
}
