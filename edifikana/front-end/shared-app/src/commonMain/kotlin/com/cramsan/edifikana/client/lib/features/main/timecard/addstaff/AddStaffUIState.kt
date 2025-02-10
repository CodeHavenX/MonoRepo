package com.cramsan.edifikana.client.lib.features.main.timecard.addstaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Add Staff screen.
 */
data class AddStaffUIState(
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Initial = AddStaffUIState(false, "")
    }
}
