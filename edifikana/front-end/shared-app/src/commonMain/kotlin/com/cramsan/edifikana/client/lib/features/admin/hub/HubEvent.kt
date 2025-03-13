package com.cramsan.edifikana.client.lib.features.admin.hub

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the Home feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class HubEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : HubEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        val event: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : HubEvent()
}
