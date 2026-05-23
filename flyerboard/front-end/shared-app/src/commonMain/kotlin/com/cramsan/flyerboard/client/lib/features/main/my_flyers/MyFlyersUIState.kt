package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the My Flyers screen.
 */
sealed class MyFlyersUIState : ViewModelUIState {
    /** The user's flyers are being fetched. */
    data object Loading : MyFlyersUIState()

    /** Flyers fetched successfully with at least one result. */
    data class Content(val flyers: List<FlyerModel>) : MyFlyersUIState()

    /** Fetch succeeded but the user has no flyers. */
    data object Empty : MyFlyersUIState()

    /** Fetch failed; detail is shown in a snackbar. */
    data object Error : MyFlyersUIState()

    companion object {
        val Initial: MyFlyersUIState = Loading
    }
}
