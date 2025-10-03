package com.cramsan.edifikana.client.lib.features.management.employee

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Employee feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class EmployeeEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : EmployeeEvent()
}
