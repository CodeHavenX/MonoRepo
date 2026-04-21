package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer Detail screen.
 */
data class FlyerDetailUIState(
    val isLoading: Boolean,
    val flyer: FlyerModel?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerDetailUIState(
            isLoading = false,
            flyer = null,
        )
    }
}
