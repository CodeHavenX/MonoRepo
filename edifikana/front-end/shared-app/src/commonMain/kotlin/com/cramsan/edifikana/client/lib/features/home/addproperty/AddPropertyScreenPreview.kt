package com.cramsan.edifikana.client.lib.features.home.addproperty

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddProperty feature screen - Default state.
 */
@Preview
@Composable
private fun AddPropertyScreenPreview() = AppTheme {
    AddPropertyContent(
        content = AddPropertyUIState(
            isLoading = false,
            orgId = null,
            selectedIcon = null,
            isUploading = false,
            uploadError = null,
        ),
        onBackSelected = {},
        onAddPropertySelected = { _, _, _, _ -> },
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the AddProperty feature screen - Uploading state.
 */
@Preview
@Composable
private fun AddPropertyScreenUploadingPreview() = AppTheme {
    AddPropertyContent(
        content = AddPropertyUIState(
            isLoading = false,
            orgId = null,
            selectedIcon = null,
            isUploading = true,
            uploadError = null,
        ),
        onBackSelected = {},
        onAddPropertySelected = { _, _, _, _ -> },
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the AddProperty feature screen - With uploaded custom image.
 */
@Preview
@Composable
private fun AddPropertyScreenWithCustomImagePreview() = AppTheme {
    AddPropertyContent(
        content = AddPropertyUIState(
            isLoading = false,
            orgId = null,
            selectedIcon = PropertyIconOptions.fromImageUrl("https://example.com/custom-image.jpg"),
            isUploading = false,
            uploadError = null,
        ),
        onBackSelected = {},
        onAddPropertySelected = { _, _, _, _ -> },
        onTriggerPhotoPicker = {},
    )
}

/**
 * Preview for the AddProperty feature screen - Upload error state.
 */
@Preview
@Composable
private fun AddPropertyScreenUploadErrorPreview() = AppTheme {
    AddPropertyContent(
        content = AddPropertyUIState(
            isLoading = false,
            orgId = null,
            selectedIcon = null,
            isUploading = false,
            uploadError = "File size exceeds 10MB limit",
        ),
        onBackSelected = {},
        onAddPropertySelected = { _, _, _, _ -> },
        onTriggerPhotoPicker = {},
    )
}
