package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Notifications feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class NotificationsEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : NotificationsEvent()
}
