package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Flyer Edit screen with content loaded.
 */
@Preview
@Composable
private fun FlyerEditScreenPreview() = AppTheme {
    FlyerEditContent(
        uiState = FlyerEditUIState(
            isLoading = false,
            isSaving = false,
            title = "Community Yard Sale",
            description = "Everything must go! Furniture, clothes, books and more.",
            expiresAt = "2026-05-01",
            errorMessage = null,
        ),
        onNavigateBack = {},
        onTitleChanged = {},
        onDescriptionChanged = {},
        onExpiresAtChanged = {},
        onSave = {},
    )
}

/**
 * Preview for the Flyer Edit screen in loading state.
 */
@Preview
@Composable
private fun FlyerEditScreenLoadingPreview() = AppTheme {
    FlyerEditContent(
        uiState = FlyerEditUIState.Initial.copy(isLoading = true),
        onNavigateBack = {},
        onTitleChanged = {},
        onDescriptionChanged = {},
        onExpiresAtChanged = {},
        onSave = {},
    )
}

/**
 * Preview for the Flyer Edit screen while saving.
 */
@Preview
@Composable
private fun FlyerEditScreenSavingPreview() = AppTheme {
    FlyerEditContent(
        uiState = FlyerEditUIState(
            isLoading = false,
            isSaving = true,
            title = "Community Yard Sale",
            description = "Everything must go!",
            expiresAt = null,
            errorMessage = null,
        ),
        onNavigateBack = {},
        onTitleChanged = {},
        onDescriptionChanged = {},
        onExpiresAtChanged = {},
        onSave = {},
    )
}
