package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun PasswordResetConfirmationContentPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState(
            isLoading = false,
            email = "user@example.com",
            errorMessages = null,
        ),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
internal fun PasswordResetConfirmationContentWithErrorPreview() {
    PasswordResetConfirmationContent(
        uiState = PasswordResetConfirmationUIState(
            isLoading = false,
            email = "user@example.com",
            errorMessages = listOf("There was an unexpected error."),
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
        uiState = PasswordResetConfirmationUIState(
            isLoading = true,
            email = "user@example.com",
            errorMessages = null,
        ),
        onResendClicked = {},
        onBackToSignInClicked = {},
        onCloseClicked = {},
    )
}
