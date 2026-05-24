package com.cramsan.framework.sample.shared.features.main.metrics

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Metrics feature.
 */
sealed class MetricsEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : MetricsEvent()
}
