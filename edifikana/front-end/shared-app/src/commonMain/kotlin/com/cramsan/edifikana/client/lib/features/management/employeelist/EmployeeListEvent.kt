package com.cramsan.edifikana.client.lib.features.management.employeelist

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the EmployeeList feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class EmployeeListEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : EmployeeListEvent()
}
