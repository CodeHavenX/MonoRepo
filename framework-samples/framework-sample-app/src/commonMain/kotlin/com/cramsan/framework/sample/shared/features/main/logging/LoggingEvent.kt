package com.cramsan.framework.sample.shared.features.main.logging

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Logging feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 */
sealed class LoggingEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : LoggingEvent()
}
