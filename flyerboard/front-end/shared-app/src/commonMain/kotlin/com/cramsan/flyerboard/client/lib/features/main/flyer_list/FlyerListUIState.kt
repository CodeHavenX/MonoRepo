package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer List screen.
 */
sealed class FlyerListUIState : ViewModelUIState {
    /** Flyers are being fetched. */
    data object Loading : FlyerListUIState()

    /** Flyers loaded successfully with at least one item. */
    data class Content(val flyers: List<FlyerModel>) : FlyerListUIState()

    /** Flyers loaded successfully but the list is empty. */
    data object Empty : FlyerListUIState()

    /** Loading failed; detail is shown in a snackbar. */
    data class Error(val message: String) : FlyerListUIState()

    companion object {
        val Initial: FlyerListUIState = Loading
    }
}
