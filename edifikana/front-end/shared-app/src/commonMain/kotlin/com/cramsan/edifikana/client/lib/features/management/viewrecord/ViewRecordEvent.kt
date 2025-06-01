package com.cramsan.edifikana.client.lib.features.management.viewrecord

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents the UI state of the View Record screen.
 */
sealed class ViewRecordEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : ViewRecordEvent()
}
