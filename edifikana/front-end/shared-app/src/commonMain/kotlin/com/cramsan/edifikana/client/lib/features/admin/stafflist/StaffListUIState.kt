package com.cramsan.edifikana.client.lib.features.admin.stafflist

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the StaffList feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class StaffListUIState(
    val isLoading: Boolean,
    val staffList: List<String>,
) : ViewModelUIState {
    companion object {
        val Initial = StaffListUIState(true, emptyList())
    }
}
