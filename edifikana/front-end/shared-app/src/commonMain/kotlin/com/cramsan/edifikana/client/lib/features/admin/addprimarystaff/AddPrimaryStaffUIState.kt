package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the AddPrimaryStaff feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class AddPrimaryStaffUIState(
    val isLoading: Boolean,
    val title: String?,
) : ViewModelUIState {
    companion object {
        val Initial = AddPrimaryStaffUIState(true, null)
    }
}
