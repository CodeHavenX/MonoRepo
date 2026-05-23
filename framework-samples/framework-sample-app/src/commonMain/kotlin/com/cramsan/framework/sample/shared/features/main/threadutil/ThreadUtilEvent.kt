package com.cramsan.framework.sample.shared.features.main.threadutil

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the ThreadUtil feature.
 */
sealed class ThreadUtilEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ThreadUtilEvent()
}
