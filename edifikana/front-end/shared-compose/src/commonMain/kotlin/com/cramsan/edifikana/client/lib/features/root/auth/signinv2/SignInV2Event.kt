package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityEvent
import kotlin.random.Random

/**
 * Sign in V2 event.
 */
sealed class SignInV2Event {

    /**
     * Noop event.
     */
    data object Noop : SignInV2Event()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()

    /**
     * Trigger activity event.
     */
    data class TriggerAuthActivityEvent(
        val authActivityEvent: AuthActivityEvent,
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()
}
