package com.cramsan.framework.sample.shared.features.main.assertutil

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the AssertUtil feature.
 */
sealed class AssertUtilEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : AssertUtilEvent()
}
