package com.cramsan.edifikana.client.lib.features.signinv2

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
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
     * Trigger main activity event.
     */
    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()

    /**
     * Launch sign in.
     */
    data class LaunchSignIn(
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()
}
