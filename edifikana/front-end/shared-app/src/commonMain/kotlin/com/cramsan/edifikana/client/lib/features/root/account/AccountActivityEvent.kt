package com.cramsan.edifikana.client.lib.features.root.account

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the account activity.
 */
sealed class AccountActivityEvent {

    /**
     * No operation.
     */
    data object Noop : AccountActivityEvent()

    /**
     * Navigate to a destination within this activity.
     */
    data class Navigate(
        val destination: AccountActivityRoute,
        val id: Int = Random.nextInt(),
    ) : AccountActivityEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AccountActivityEvent()
}
