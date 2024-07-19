package com.cramsan.edifikana.client.lib.features.signinv2

import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import kotlin.random.Random

sealed class SignInV2Event {
    data object Noop : SignInV2Event()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()

    data class LaunchSignIn(
        val id: Int = Random.nextInt(),
    ) : SignInV2Event()
}
