package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the account.
 */
sealed class AccountEvent : ViewModelEvent {

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
}
