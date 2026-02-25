package com.cramsan.edifikana.client.lib.features.home.propertydetail

import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the PropertyDetail feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyDetailUIState(
    val isLoading: Boolean,
    val propertyId: PropertyId?,
    val name: String,
    val address: String,
    val imageUrl: String?,
    val isEditMode: Boolean,
    val selectedIcon: ImageOptionUIModel? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
) : ViewModelUIState {
    companion object {
        val Initial = PropertyDetailUIState(
            isLoading = true,
            propertyId = null,
            name = "",
            address = "",
            imageUrl = null,
            isEditMode = false,
            selectedIcon = null,
            isUploading = false,
            uploadError = null,
        )
    }
}
