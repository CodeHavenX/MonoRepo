package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Moderation Queue screen.
 */
data class ModerationQueueUIState(
    val isLoading: Boolean,
    val pendingFlyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = ModerationQueueUIState(
            isLoading = false,
            pendingFlyers = emptyList(),
            errorMessage = null,
        )
    }
}
