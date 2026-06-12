package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer List screen.
 */
sealed class FlyerListUIState : ViewModelUIState {
    /** The current search query, shared by all states so the search bar always has a value. */
    abstract val query: String

    /** Flyers are being fetched. */
    data class Loading(override val query: String = "") : FlyerListUIState()

    /** Flyers loaded successfully with at least one item. */
    data class Content(val flyers: List<FlyerModel>, override val query: String = "") : FlyerListUIState()

    /** Flyers loaded successfully but the list is empty. */
    data class Empty(override val query: String = "") : FlyerListUIState()

    /** Loading failed; detail is shown in a snackbar. */
    data class Error(val message: String, override val query: String = "") : FlyerListUIState()

    companion object {
        val Initial: FlyerListUIState = Loading()
    }
}
