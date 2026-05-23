package com.cramsan.flyerboard.client.lib.features.main.archive

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Archive screen.
 */
sealed class ArchiveUIState : ViewModelUIState {
    /** The current search query, shared by all states so the search bar always has a value. */
    abstract val query: String

    /** Archived flyers are being fetched. */
    data class Loading(override val query: String = "") : ArchiveUIState()

    /** Flyers fetched successfully with at least one result. */
    data class Content(val flyers: List<FlyerModel>, override val query: String = "") : ArchiveUIState()

    /** Fetch succeeded but no flyers match the current query. */
    data class Empty(override val query: String = "") : ArchiveUIState()

    /** Fetch failed; detail is shown in a snackbar. */
    data class Error(override val query: String = "") : ArchiveUIState()

    companion object {
        val Initial: ArchiveUIState = Loading()
    }
}
