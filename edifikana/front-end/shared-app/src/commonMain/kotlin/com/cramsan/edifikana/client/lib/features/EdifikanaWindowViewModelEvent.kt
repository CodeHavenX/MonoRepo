package com.cramsan.edifikana.client.lib.features

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
}
