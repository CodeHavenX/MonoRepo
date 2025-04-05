package com.cramsan.edifikana.client.lib.features.admin.staff

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Staff feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class StaffEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : StaffEvent()
}
