package com.cramsan.edifikana.client.lib.features.home.propertydetail

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.PropertyId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the PropertyDetail feature screen.
 */
@Preview
@Composable
private fun PropertyDetailScreenPreview() = AppTheme {
    PropertyDetailContent(
        content = PropertyDetailUIState(
            isLoading = false,
            propertyId = PropertyId("preview-property-id"),
            name = "Sample Property",
            address = "123 Main Street, City, State 12345",
            imageUrl = "drawable:CASA",
            isEditMode = false,
        ),
        onBackSelected = {},
        onEditSelected = {},
        onCancelEdit = {},
        onSaveProperty = {},
        onDeleteProperty = {},
        onNameChanged = {},
        onAddressChanged = {},
        onImageUrlChanged = {},
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the PropertyDetail feature screen in edit mode.
 */
@Preview
@Composable
private fun PropertyDetailScreenEditModePreview() = AppTheme {
    PropertyDetailContent(
        content = PropertyDetailUIState(
            isLoading = false,
            propertyId = PropertyId("preview-property-id"),
            name = "Sample Property",
            address = "123 Main Street, City, State 12345",
            imageUrl = "drawable:QUINTA",
            isEditMode = true,
        ),
        onBackSelected = {},
        onEditSelected = {},
        onCancelEdit = {},
        onSaveProperty = {},
        onDeleteProperty = {},
        onNameChanged = {},
        onAddressChanged = {},
        onImageUrlChanged = {},
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the PropertyDetail feature screen - Edit mode with uploading state.
 */
@Preview
@Composable
private fun PropertyDetailScreenUploadingPreview() = AppTheme {
    PropertyDetailContent(
        content = PropertyDetailUIState(
            isLoading = false,
            propertyId = PropertyId("preview-property-id"),
            name = "Sample Property",
            address = "123 Main Street, City, State 12345",
            imageUrl = "drawable:QUINTA",
            isEditMode = true,
            selectedIcon = null,
            isUploading = true,
            uploadError = null,
        ),
        onBackSelected = {},
        onEditSelected = {},
        onCancelEdit = {},
        onSaveProperty = {},
        onDeleteProperty = {},
        onNameChanged = {},
        onAddressChanged = {},
        onImageUrlChanged = {},
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the PropertyDetail feature screen - Edit mode with custom image selected.
 */
@Preview
@Composable
private fun PropertyDetailScreenWithCustomImagePreview() = AppTheme {
    PropertyDetailContent(
        content = PropertyDetailUIState(
            isLoading = false,
            propertyId = PropertyId("preview-property-id"),
            name = "Sample Property",
            address = "123 Main Street, City, State 12345",
            imageUrl = "drawable:QUINTA",
            isEditMode = true,
            selectedIcon = PropertyIconOptions.fromImageUrl("https://example.com/custom-image.jpg"),
            isUploading = false,
            uploadError = null,
        ),
        onBackSelected = {},
        onEditSelected = {},
        onCancelEdit = {},
        onSaveProperty = {},
        onDeleteProperty = {},
        onNameChanged = {},
        onAddressChanged = {},
        onImageUrlChanged = {},
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the PropertyDetail feature screen - Edit mode with upload error.
 */
@Preview
@Composable
private fun PropertyDetailScreenUploadErrorPreview() = AppTheme {
    PropertyDetailContent(
        content = PropertyDetailUIState(
            isLoading = false,
            propertyId = PropertyId("preview-property-id"),
            name = "Sample Property",
            address = "123 Main Street, City, State 12345",
            imageUrl = "drawable:QUINTA",
            isEditMode = true,
            selectedIcon = null,
            isUploading = false,
            uploadError = "File size exceeds 10MB limit",
        ),
        onBackSelected = {},
        onEditSelected = {},
        onCancelEdit = {},
        onSaveProperty = {},
        onDeleteProperty = {},
        onNameChanged = {},
        onAddressChanged = {},
        onImageUrlChanged = {},
        onTriggerPhotoPicker = {},
    )
}
