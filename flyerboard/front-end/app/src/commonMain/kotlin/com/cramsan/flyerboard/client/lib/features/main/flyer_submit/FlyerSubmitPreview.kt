package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Flyer Submit screen with all fields blank and no file selected.
 */
@DevicePreviews
@Composable
private fun FlyerSubmitEmptyPreview() =
    AppTheme(dynamicColor = false) {
        FlyerSubmitContent(
            uiState = FlyerSubmitUIState.Initial,
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSubmit = {},
        )
    }

/**
 * Preview for the Flyer Submit screen with a file name displayed.
 */
@ScreenPreviews
@Composable
private fun FlyerSubmitWithFilePreview() =
    AppTheme(dynamicColor = false) {
        FlyerSubmitContent(
            uiState =
            FlyerSubmitUIState(
                status = SubmitStatus.Idle,
                title = "Summer Concert",
                description = "Join us for an outdoor concert in the park.",
                expiresAt = "2026-08-01",
                selectedFileName = "concert-poster.jpg",
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSubmit = {},
        )
    }

/**
 * Preview for the Flyer Submit screen while the submission is in progress.
 */
@ScreenPreviews
@Composable
private fun FlyerSubmitSavingPreview() =
    AppTheme(dynamicColor = false) {
        FlyerSubmitContent(
            uiState =
            FlyerSubmitUIState(
                status = SubmitStatus.Submitting,
                title = "Summer Concert",
                description = "Join us for an outdoor concert in the park.",
                expiresAt = null,
                selectedFileName = "concert-poster.jpg",
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSubmit = {},
        )
    }

/**
 * Preview for the Flyer Submit screen with an error message visible.
 */
@ScreenPreviews
@Composable
private fun FlyerSubmitErrorPreview() =
    AppTheme(dynamicColor = false) {
        FlyerSubmitContent(
            uiState =
            FlyerSubmitUIState(
                status = SubmitStatus.Failed("Failed to upload flyer. Please try again."),
                title = "Summer Concert",
                description = "Join us for an outdoor concert in the park.",
                expiresAt = null,
                selectedFileName = null,
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSubmit = {},
        )
    }
