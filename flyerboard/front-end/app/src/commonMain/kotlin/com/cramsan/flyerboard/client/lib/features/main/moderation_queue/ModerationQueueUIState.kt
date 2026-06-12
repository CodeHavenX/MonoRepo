package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Moderation Queue screen.
 */
sealed class ModerationQueueUIState : ViewModelUIState {
    /** Pending flyers are being fetched. */
    data object Loading : ModerationQueueUIState()

    /** Pending flyers fetched successfully with at least one result. */
    data class Content(val flyers: List<FlyerModel>, val pendingRejectionFlyerId: FlyerId? = null) :
        ModerationQueueUIState()

    /** Fetch succeeded but there are no pending flyers. */
    data object Empty : ModerationQueueUIState()

    /** Fetch failed; detail is shown in a snackbar. */
    data object Error : ModerationQueueUIState()

    companion object {
        val Initial: ModerationQueueUIState = Loading
    }
}
