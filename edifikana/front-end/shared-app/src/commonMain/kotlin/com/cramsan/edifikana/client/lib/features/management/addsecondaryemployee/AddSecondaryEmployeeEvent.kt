package com.cramsan.edifikana.client.lib.features.management.addsecondaryemployee

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the AddSecondary feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class AddSecondaryEmployeeEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : AddSecondaryEmployeeEvent()
}
