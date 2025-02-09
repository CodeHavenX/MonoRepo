package com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI State for the View Staff screen.
 */
data class ViewStaffUIState(
    val content: ViewStaffUIModel?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ViewStaffUIState(
            content = null,
            isLoading = true,
        )
    }
}
