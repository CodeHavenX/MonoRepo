package com.cramsan.edifikana.client.lib.features.main.viewrecord

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the View Record screen.
 */
data class ViewRecordUIState(
    val record: ViewRecordUIModel?,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = ViewRecordUIState(null, true, "")
    }
}
