package com.cramsan.edifikana.client.lib.features.management.viewemployee

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the View Employee screen.
 */
data class ViewEmployeeUIState(
    val isLoading: Boolean,
    val employee: ViewEmployeeUIModel.EmployeeUIModel?,
    val records: List<ViewEmployeeUIModel.TimeCardRecordUIModel>,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = ViewEmployeeUIState(
            true,
            null,
            emptyList(),
            "",
        )
    }
}
