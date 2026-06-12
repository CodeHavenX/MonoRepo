package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
internal fun PasswordResetConfirmationContentPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState.Stable(email = "user@example.com"),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}

@ScreenPreviews
@Composable
internal fun PasswordResetConfirmationContentWithErrorPreview() {
    PasswordResetConfirmationContent(
        uiState =
        PasswordResetConfirmationUIState.Error(
            email = "user@example.com",
            messages = listOf("There was an unexpected error."),
        ),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}

@ScreenPreviews
@Composable
internal fun PasswordResetConfirmationContentLoadingPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState.Loading(email = "user@example.com"),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}
