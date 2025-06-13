package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the StaffList feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class StaffListEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : StaffListEvent()
}
