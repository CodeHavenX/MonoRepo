package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
internal fun PasswordResetContentPreview() {
    PasswordResetContent(
        uiState = PasswordResetUIState.Initial,
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}

@ScreenPreviews
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

@ScreenPreviews
@Composable
internal fun PasswordResetContentLoadingPreview() {
    PasswordResetContent(
        uiState = PasswordResetUIState.Loading(email = "user@example.com"),
        onEmailValueChange = {},
        onSendClicked = {},
        onCloseClicked = {},
    )
}
