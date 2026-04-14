package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the FlyerBoardApplicationViewModel.
 */
sealed class FlyerBoardApplicationViewModelEvent : ViewModelEvent {

    /**
     * Noop event.
     */
    data object Noop : FlyerBoardApplicationViewModelEvent()
}
