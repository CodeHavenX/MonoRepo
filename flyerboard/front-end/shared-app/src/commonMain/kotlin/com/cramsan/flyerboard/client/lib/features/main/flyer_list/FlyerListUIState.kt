package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer List screen.
 */
data class FlyerListUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerListUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        )
    }
}
