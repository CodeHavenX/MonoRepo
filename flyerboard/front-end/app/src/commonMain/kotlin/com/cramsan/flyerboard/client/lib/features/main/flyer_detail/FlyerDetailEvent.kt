package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Flyer Detail feature.
 */
sealed class FlyerDetailEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : FlyerDetailEvent()
}
