package com.cramsan.edifikana.client.lib.features.window

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the whole window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class EdifikanaWindowViewModelEvent : ViewModelEvent {

    /**
     * Wrapper for [EdifikanaWindowsEvent] to be used in the view model.
     */
    data class EdifikanaWindowEventWrapper(
        val event: EdifikanaWindowsEvent,
    ) : EdifikanaWindowViewModelEvent()

    /**
     * Wrapper for [EdifikanaWindowDelegatedEvent] to be used in the view model.
     * These are events that flow from the window back to feature screens
     * (e.g., photo picker results, camera results).
     */
    data class EdifikanaDelegatedEventWrapper(
        val event: EdifikanaWindowDelegatedEvent,
    ) : EdifikanaWindowViewModelEvent()
}
