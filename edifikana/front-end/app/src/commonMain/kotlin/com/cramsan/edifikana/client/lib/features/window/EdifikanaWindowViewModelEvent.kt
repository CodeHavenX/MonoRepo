package com.cramsan.edifikana.client.lib.features.window

import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.core.compose.navigation.NavigateBackWithResult

/**
 * Events that can be triggered in the whole window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class EdifikanaWindowViewModelEvent : ViewModelEvent {
    /**
     * Wrapper for [EdifikanaWindowsEvent] to be used in the view model.
     */
    data class EdifikanaWindowEventWrapper(val event: EdifikanaWindowsEvent) : EdifikanaWindowViewModelEvent()

    /**
     * Carries a [NavigateBackWithResult] event from the window event bus to the window screen.
     */
    data class NavBackWithResult(val result: NavigateBackWithResult) : EdifikanaWindowViewModelEvent()
}
