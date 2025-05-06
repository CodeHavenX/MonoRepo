package com.cramsan.edifikana.client.lib.features

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class EdifikanaApplicationViewModelEvent : ViewModelEvent {

    /**
     * Wrapper for [EdifikanaApplicationEvent] to be used in the view model.
     */
    data class EdifikanaApplicationEventWrapper(
        val event: EdifikanaApplicationEvent,
    ) : EdifikanaApplicationViewModelEvent()
}
