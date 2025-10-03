package com.cramsan.edifikana.client.lib.features.management.viewemployee

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the View Employee screen.
 */
sealed class ViewEmployeeEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ViewEmployeeEvent()
}
