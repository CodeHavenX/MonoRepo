package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Sign In feature.
 */
sealed class SignInEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : SignInEvent()
}
