package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun PasswordResetContentPreview() {
    PasswordResetContent(
        uiState = PasswordResetUIState.Initial,
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
internal fun PasswordResetContentWithErrorPreview() {
    PasswordResetContent(
        uiState = PasswordResetUIState(
            isLoading = false,
            email = "user@example.com",
            errorMessages = listOf("Please enter a valid email address."),
        ),
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
internal fun PasswordResetContentLoadingPreview() {
    PasswordResetContent(
        uiState = PasswordResetUIState(
            isLoading = true,
            email = "user@example.com",
            errorMessages = null,
        ),
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}
