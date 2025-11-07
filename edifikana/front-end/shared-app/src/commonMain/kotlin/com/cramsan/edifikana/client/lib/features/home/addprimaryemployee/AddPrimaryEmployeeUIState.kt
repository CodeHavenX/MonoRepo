package com.cramsan.edifikana.client.lib.features.home.addprimaryemployee

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the AddPrimaryEmployee feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class AddPrimaryEmployeeUIState(
    val isLoading: Boolean,
    val title: String?,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = AddPrimaryEmployeeUIState(
            isLoading = false,
            title = null,
            errorMessage = null,
        )
    }
}
