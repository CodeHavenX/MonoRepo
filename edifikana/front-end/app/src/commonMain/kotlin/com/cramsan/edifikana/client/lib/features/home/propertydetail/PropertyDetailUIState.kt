package com.cramsan.edifikana.client.lib.features.home.propertydetail

import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.core.compose.ViewModelUIState

/** Dialog state for the PropertyDetail screen. */
sealed class PropertyDetailDialogState {
    /** No dialog is shown. */
    data object None : PropertyDetailDialogState()

    /** Image selector bottom sheet. */
    data object ShowImageSelector : PropertyDetailDialogState()

    /** Delete confirmation dialog. */
    data object ConfirmDelete : PropertyDetailDialogState()
}

/**
 * UI state of the PropertyDetail feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyDetailUIState(
    val isLoading: Boolean,
    val propertyId: PropertyId?,
    val organizationId: OrganizationId? = null,
    val name: String,
    val address: String,
    val imageUrl: String?,
    val isEditMode: Boolean,
    val selectedIcon: ImageOptionUIModel? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
    val dialog: PropertyDetailDialogState = PropertyDetailDialogState.None,
) : ViewModelUIState {
    companion object {
        val Initial =
            PropertyDetailUIState(
                isLoading = true,
                propertyId = null,
                name = "",
                address = "",
                imageUrl = null,
                isEditMode = false,
                selectedIcon = null,
                isUploading = false,
                uploadError = null,
                dialog = PropertyDetailDialogState.None,
            )
    }
}
