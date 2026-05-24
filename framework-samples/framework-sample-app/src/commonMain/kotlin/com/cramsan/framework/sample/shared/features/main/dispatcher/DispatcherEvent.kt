package com.cramsan.framework.sample.shared.features.main.dispatcher

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the DispatcherProvider feature.
 */
sealed class DispatcherEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : DispatcherEvent()
}
