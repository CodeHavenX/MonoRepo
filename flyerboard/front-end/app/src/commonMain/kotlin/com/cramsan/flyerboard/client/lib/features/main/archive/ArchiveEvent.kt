package com.cramsan.flyerboard.client.lib.features.main.archive

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Archive feature.
 */
sealed class ArchiveEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ArchiveEvent()
}
