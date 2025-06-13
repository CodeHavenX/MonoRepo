package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Validation feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class OtpValidationEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : OtpValidationEvent()
}
