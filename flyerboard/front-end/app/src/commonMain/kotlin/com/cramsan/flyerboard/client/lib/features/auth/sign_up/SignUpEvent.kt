package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Sign Up feature.
 */
sealed class SignUpEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : SignUpEvent()
}
