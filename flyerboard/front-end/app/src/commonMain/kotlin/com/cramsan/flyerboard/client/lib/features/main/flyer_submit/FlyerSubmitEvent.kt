package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Flyer Submit feature.
 */
sealed class FlyerSubmitEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : FlyerSubmitEvent()
}
