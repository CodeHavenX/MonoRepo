package com.cramsan.edifikana.client.lib.features.root.auth.signup

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityEvent
import kotlin.random.Random

/**
 * Sign Up feature event.
 */
sealed class SignUpEvent {

    /**
     * Noop event.
     */
    data object Noop : SignUpEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : SignUpEvent()

    /**
     * Trigger activity event.
     */
    data class TriggerAuthActivityEvent(
        val authActivityEvent: AuthActivityEvent,
        val id: Int = Random.nextInt(),
    ) : SignUpEvent()
}
