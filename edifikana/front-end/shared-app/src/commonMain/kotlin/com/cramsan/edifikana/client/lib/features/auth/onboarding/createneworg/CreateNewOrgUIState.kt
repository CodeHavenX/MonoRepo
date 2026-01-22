package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the CreateNewOrg feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class CreateNewOrgUIState(
    val organizationName: String,
    val organizationDescription: String,
    val isLoading: Boolean,
    val isButtonEnabled: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = CreateNewOrgUIState(
            organizationName = "",
            organizationDescription = "",
            isLoading = false,
            isButtonEnabled = false,
        )
    }
}
