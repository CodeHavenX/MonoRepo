package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the whole window. These events are intended to be
 * triggered by a feature screen, and it will be handled by the window.
 */
sealed class FlyerBoardWindowViewModelEvent : ViewModelEvent {

    /**
     * Wrapper for [FlyerBoardWindowsEvent] to be used in the view model.
     */
    data class FlyerBoardWindowEventWrapper(
        val event: FlyerBoardWindowsEvent,
    ) : FlyerBoardWindowViewModelEvent()
}
