package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the AddProperty feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class AddPropertyUIState(
    val isLoading: Boolean,
    val orgId: OrganizationId?,
    val selectedIcon: ImageOptionUIModel? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
) : ViewModelUIState {
    companion object {
        val Initial = AddPropertyUIState(
            isLoading = false,
            orgId = null,
            selectedIcon = null,
            isUploading = false,
            uploadError = null,
        )
    }
}
