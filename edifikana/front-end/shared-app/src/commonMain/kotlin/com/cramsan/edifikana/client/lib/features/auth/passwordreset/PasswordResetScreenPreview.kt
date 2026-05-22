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
        uiState =
        PasswordResetUIState.Error(
            email = "user@example.com",
            messages = listOf("Please enter a valid email address."),
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
        uiState = PasswordResetUIState.Loading(email = "user@example.com"),
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}
