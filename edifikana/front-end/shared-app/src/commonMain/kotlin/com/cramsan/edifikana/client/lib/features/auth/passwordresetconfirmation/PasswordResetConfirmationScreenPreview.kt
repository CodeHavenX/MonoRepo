package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun PasswordResetConfirmationContentPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState.Stable(email = "user@example.com"),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
internal fun PasswordResetConfirmationContentWithErrorPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState.Error(
            email = "user@example.com",
            messages = listOf("There was an unexpected error."),
        ),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
internal fun PasswordResetConfirmationContentLoadingPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState.Loading(email = "user@example.com"),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}
