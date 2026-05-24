package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun FlyerEditScreenPreview() =
    AppTheme {
        FlyerEditContent(
            uiState =
            FlyerEditUIState.Editing(
                isSaving = false,
                title = "Community Yard Sale",
                description = "Everything must go! Furniture, clothes, books and more.",
                expiresAt = "2026-05-01",
                errorMessage = null,
                selectedFileName = null,
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSave = {},
        )
    }

@Preview
@Composable
private fun FlyerEditScreenLoadingPreview() =
    AppTheme {
        FlyerEditContent(
            uiState = FlyerEditUIState.Loading,
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSave = {},
        )
    }

@Preview
@Composable
private fun FlyerEditScreenSavingPreview() =
    AppTheme {
        FlyerEditContent(
            uiState =
            FlyerEditUIState.Editing(
                isSaving = true,
                title = "Community Yard Sale",
                description = "Everything must go!",
                expiresAt = null,
                errorMessage = null,
                selectedFileName = null,
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSave = {},
        )
    }

@Preview
@Composable
private fun FlyerEditNoFilePreview() =
    AppTheme {
        FlyerEditContent(
            uiState =
            FlyerEditUIState.Editing(
                isSaving = false,
                title = "Block Party",
                description = "Join us this Saturday!",
                expiresAt = null,
                errorMessage = null,
                selectedFileName = null,
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSave = {},
        )
    }

@Preview
@Composable
private fun FlyerEditWithFilePreview() =
    AppTheme {
        FlyerEditContent(
            uiState =
            FlyerEditUIState.Editing(
                isSaving = false,
                title = "Block Party",
                description = "Join us this Saturday!",
                expiresAt = null,
                errorMessage = null,
                selectedFileName = "block_party.jpg",
            ),
            onNavigateBack = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onExpiresAtChanged = {},
            onFileSelected = { _, _, _ -> },
            onSave = {},
        )
    }
