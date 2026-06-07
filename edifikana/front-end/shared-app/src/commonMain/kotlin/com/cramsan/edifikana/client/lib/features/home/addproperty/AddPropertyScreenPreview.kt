package com.cramsan.edifikana.client.lib.features.home.addproperty

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.home.shared.PropertyIconOptions
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the AddProperty feature screen - Default state.
 */
@ScreenPreviews
@Composable
private fun AddPropertyScreenPreview() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = null,
                isUploading = false,
                uploadError = null,
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }

/**
 * Preview for the AddProperty feature screen - Uploading state.
 */
@ScreenPreviews
@Composable
private fun AddPropertyScreenUploadingPreview() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = null,
                isUploading = true,
                uploadError = null,
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }

/**
 * Preview for the AddProperty feature screen - With uploaded custom image.
 */
@ScreenPreviews
@Composable
private fun AddPropertyScreenWithCustomImagePreview() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = PropertyIconOptions.fromImageUrl("https://example.com/custom-image.jpg"),
                isUploading = false,
                uploadError = null,
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }

/**
 * Preview for the AddProperty feature screen - Upload error state.
 */
@ScreenPreviews
@Composable
private fun AddPropertyScreenUploadErrorPreview() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = null,
                isUploading = false,
                uploadError = "File size exceeds 10MB limit",
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }

@ScreenPreviews
@Composable
private fun AddPropertyScreenPreview_ES() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = null,
                isUploading = false,
                uploadError = null,
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }

@ScreenPreviews
@Composable
private fun AddPropertyScreenUploadingPreview_ES() =
    AppTheme {
        AddPropertyContent(
            content =
            AddPropertyUIState(
                isLoading = false,
                orgId = null,
                selectedIcon = null,
                isUploading = true,
                uploadError = null,
            ),
            onBackSelected = {},
            onAddPropertySelected = { _, _, _ -> },
            onOpenSelectorSelected = { },
        )
    }
