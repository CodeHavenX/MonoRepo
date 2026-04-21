package com.cramsan.flyerboard.client.lib.features.main.archive

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Archive screen.
 */
data class ArchiveUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = ArchiveUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        )
    }
}
