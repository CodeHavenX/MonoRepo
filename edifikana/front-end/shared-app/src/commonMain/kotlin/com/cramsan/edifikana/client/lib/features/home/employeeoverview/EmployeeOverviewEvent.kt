package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the EmployeeOverview feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class EmployeeOverviewEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : EmployeeOverviewEvent()
}
