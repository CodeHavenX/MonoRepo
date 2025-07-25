package com.cramsan.edifikana.client.lib.features.management.addrecord

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Add Record screen.
 */
data class AddRecordUIState(
    val records: List<com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = AddRecordUIState(emptyList(), false, "")
    }
}
