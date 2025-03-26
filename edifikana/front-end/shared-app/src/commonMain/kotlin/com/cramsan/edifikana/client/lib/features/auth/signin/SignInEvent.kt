package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Sign in event.
 */
sealed class SignInEvent : ViewModelEvent {

    /**
     * Noop event.
     */
    data object Noop : SignInEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val edifikanaApplicationEvent: EdifikanaApplicationEvent,
    ) : SignInEvent()
}
