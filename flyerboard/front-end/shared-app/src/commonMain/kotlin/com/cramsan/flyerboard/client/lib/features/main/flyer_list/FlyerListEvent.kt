package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Flyer List feature.
 */
sealed class FlyerListEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : FlyerListEvent()
}
