package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the My Flyers screen.
 */
data class MyFlyersUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = MyFlyersUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        )
    }
}
