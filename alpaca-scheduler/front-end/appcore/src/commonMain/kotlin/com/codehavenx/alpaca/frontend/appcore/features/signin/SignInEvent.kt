package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the Sign In screen.
 */
sealed class SignInEvent {

    /**
     * No operation
     */
    data object Noop : SignInEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : SignInEvent()
}