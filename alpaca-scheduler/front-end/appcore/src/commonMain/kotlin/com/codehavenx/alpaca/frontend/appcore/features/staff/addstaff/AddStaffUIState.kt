package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Add Staff screen.
 */
data class AddStaffUIState(
    val content: AddStaffUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = AddStaffUIState(
            content = AddStaffUIModel(""),
            isLoading = false,
        )
    }
}
