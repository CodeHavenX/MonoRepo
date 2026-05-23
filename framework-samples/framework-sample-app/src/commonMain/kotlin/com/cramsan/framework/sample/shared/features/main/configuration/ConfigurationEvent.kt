package com.cramsan.framework.sample.shared.features.main.configuration

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Configuration feature.
 */
sealed class ConfigurationEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ConfigurationEvent()
}
