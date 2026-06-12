package com.cramsan.edifikana.client.lib.features.debug.screenselector

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the ScreenSelector feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class ScreenSelectorEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ScreenSelectorEvent()
}
