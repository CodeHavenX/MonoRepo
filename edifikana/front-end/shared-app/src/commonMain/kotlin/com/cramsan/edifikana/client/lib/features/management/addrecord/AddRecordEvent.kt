package com.cramsan.edifikana.client.lib.features.management.addrecord

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the Add Record screen.
 */
sealed class AddRecordEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : AddRecordEvent()
}
