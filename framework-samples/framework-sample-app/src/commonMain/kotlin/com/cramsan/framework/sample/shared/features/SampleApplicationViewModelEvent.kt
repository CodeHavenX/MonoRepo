package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class SampleApplicationViewModelEvent : ViewModelEvent {

    /**
     * Wrapper for [SampleWindowEvent] to be used in the view model.
     */
    data class SampleApplicationEventWrapper(
        val event: SampleWindowEvent,
    ) : SampleApplicationViewModelEvent()
}
