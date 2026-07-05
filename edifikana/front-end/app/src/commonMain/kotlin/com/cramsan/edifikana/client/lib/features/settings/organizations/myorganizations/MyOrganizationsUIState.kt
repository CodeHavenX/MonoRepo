package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the MyOrganizations feature.
 */
data class MyOrganizationsUIState(val isLoading: Boolean, val organizations: List<OrgListItemUIModel>) :
    ViewModelUIState {
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
