package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.WindowEvent

/**
 * Delegated events that can be triggered in the current window. These events
 * are intended to be observed within a feature screen to be able to handle
 * the event.
 */
sealed class SampleWindowDelegatedEvent : WindowEvent {

    /**
     * Noop event. Placeholder until we add more events.
     */
    data object Noop : SampleWindowDelegatedEvent()
}
