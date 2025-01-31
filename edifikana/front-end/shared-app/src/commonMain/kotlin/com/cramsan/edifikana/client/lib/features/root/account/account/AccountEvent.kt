package com.cramsan.edifikana.client.lib.features.root.account.account

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.account.AccountActivityEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the account.
 */
sealed class AccountEvent {

    /**
     * No operation.
     */
    data object Noop : AccountEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AccountEvent()

    /**
     * Trigger activity event.
     */
    data class TriggerAccountActivityEvent(
        val accountActivityEvent: AccountActivityEvent,
        val id: Int = Random.nextInt(),
    ) : AccountEvent()
}
