package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Update Staff screen.
 */
data class UpdateStaffUIState(
    val content: UpdateStaffUIModel?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = UpdateStaffUIState(
            content = null,
            isLoading = false,
        )
    }
}
