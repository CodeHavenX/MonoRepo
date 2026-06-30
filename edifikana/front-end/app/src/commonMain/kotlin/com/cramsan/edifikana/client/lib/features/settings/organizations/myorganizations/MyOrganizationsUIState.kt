package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.core.compose.ViewModelUIState

/** Dialog state for the MyOrganizations screen. */
sealed class MyOrganizationsDialogState {
    /** No dialog is shown. */
    data object None : MyOrganizationsDialogState()

    /** Confirmation dialog asking the user to switch to [orgId]. */
    data class ConfirmSwitchOrg(val orgId: OrganizationId) : MyOrganizationsDialogState()
}

/**
 * UI state of the MyOrganizations feature.
 */
data class MyOrganizationsUIState(
    val isLoading: Boolean,
    val organizations: List<OrgListItemUIModel>,
    val dialog: MyOrganizationsDialogState = MyOrganizationsDialogState.None,
) : ViewModelUIState {
    companion object {
        /** Initial loading state. */
        val Initial = MyOrganizationsUIState(isLoading = true, organizations = emptyList())
    }
}

/**
 * UI model representing a single organization item in the list.
 */
data class OrgListItemUIModel(
    val orgId: OrganizationId,
    val name: String,
    val roleLabel: String,
    val isActive: Boolean,
)
