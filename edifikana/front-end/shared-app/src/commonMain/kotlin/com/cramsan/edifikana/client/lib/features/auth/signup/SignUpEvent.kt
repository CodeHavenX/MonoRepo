package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Sign Up feature event.
 */
sealed class SignUpEvent : ViewModelEvent {

    /**
     * Noop event.
     */
    data object Noop : SignUpEvent()

    /**
     * Trigger application event.
     */
    data class TriggerEdifikanaApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
    ) : SignUpEvent()
}
