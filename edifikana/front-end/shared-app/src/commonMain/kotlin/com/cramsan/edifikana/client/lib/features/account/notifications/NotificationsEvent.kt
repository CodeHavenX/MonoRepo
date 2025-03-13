package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the Notifications feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class NotificationsEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : NotificationsEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : NotificationsEvent()
}
