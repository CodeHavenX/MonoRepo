package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer Detail screen.
 */
sealed class FlyerDetailUIState : ViewModelUIState {
    /** The flyer is being fetched. */
    data object Loading : FlyerDetailUIState()

    /** The flyer was fetched successfully. */
    data class Content(val flyer: FlyerModel, val canEdit: Boolean = false, val canModerate: Boolean = false) :
        FlyerDetailUIState()

    /** No flyer was found for the requested ID. */
    data object NotFound : FlyerDetailUIState()

    companion object {
        val Initial: FlyerDetailUIState = Loading
    }
}
