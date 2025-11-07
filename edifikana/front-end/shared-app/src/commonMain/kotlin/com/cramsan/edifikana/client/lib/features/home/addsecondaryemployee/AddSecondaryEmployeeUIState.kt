package com.cramsan.edifikana.client.lib.features.home.addsecondaryemployee

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the AddSecondary feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class AddSecondaryEmployeeUIState(
    val isLoading: Boolean,
    val title: String?,
) : ViewModelUIState {
    companion object {
        val Initial = AddSecondaryEmployeeUIState(false, null)
    }
}
