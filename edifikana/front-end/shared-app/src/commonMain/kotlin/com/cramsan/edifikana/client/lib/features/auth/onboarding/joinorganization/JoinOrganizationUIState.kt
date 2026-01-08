package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the JoinOrganization feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class JoinOrganizationUIState(
    val organizationNameOrCode: String,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = JoinOrganizationUIState(
            organizationNameOrCode = "",
            isLoading = false,
        )
    }
}
