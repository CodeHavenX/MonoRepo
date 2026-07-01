package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.core.compose.navigation.NavigateBackWithResult

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class SampleApplicationViewModelEvent : ViewModelEvent {
    /**
     * Wrapper for [SampleWindowEvent] to be used in the view model.
     */
    data class SampleApplicationEventWrapper(val event: SampleWindowEvent) : SampleApplicationViewModelEvent()

    /**
     * Carries a [NavigateBackWithResult] event from the window event bus to the application screen.
     */
    data class NavBackWithResult(val result: NavigateBackWithResult) : SampleApplicationViewModelEvent()
}
