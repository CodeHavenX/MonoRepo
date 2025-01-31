package com.cramsan.edifikana.client.lib.features.root.debug

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the Debug activity.
 */
sealed class DebugActivityEvent {

    /**
     * No operation.
     */
    data object Noop : DebugActivityEvent()

    /**
     * Navigate to a destination within this activity.
     */
    data class Navigate(
        val destination: DebugRouteDestination,
        val id: Int = Random.nextInt(),
    ) : DebugActivityEvent()

    /**
     * Close the Debug activity.
     */
    data class CloseActivity(
        val id: Int = Random.nextInt(),
    ) : DebugActivityEvent()

    /**
     * Trigger application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : DebugActivityEvent()
}
