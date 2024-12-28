package com.cramsan.edifikana.client.lib.features.root.admin

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the Admin activity.
 */
sealed class AdminActivityEvent {

    /**
     * No operation.
     */
    data object Noop : AdminActivityEvent()

    /**
     * Navigate to a destination within this activity.
     */
    data class Navigate(
        val destination: AdminRouteDestination,
        val id: Int = Random.nextInt(),
    ) : AdminActivityEvent()

    /**
     * Close the Admin activity.
     */
    data class CloseActivity(
        val id: Int = Random.nextInt(),
    ) : AdminActivityEvent()

    /**
     * Trigger application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AdminActivityEvent()
}
