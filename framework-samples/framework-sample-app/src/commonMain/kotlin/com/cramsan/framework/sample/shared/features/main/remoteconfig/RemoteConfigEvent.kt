package com.cramsan.framework.sample.shared.features.main.remoteconfig

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the RemoteConfig feature.
 */
sealed class RemoteConfigEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : RemoteConfigEvent()
}
