package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
internal fun InvitationAcceptNewUserPreview() {
    InvitationAcceptContent(
        uiState =
        InvitationAcceptUIState(
            isLoading = false,
            inviteEmail = "jane@example.com",
            orgName = "Acme Corp",
            role = "Staff",
            isUserSignedIn = false,
            isInviteValid = true,
        ),
        onFullNameChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onAcceptClicked = {},
        onSignInClicked = {},
    )
}

@ScreenPreviews
@Composable
internal fun InvitationAcceptExistingUserPreview() {
    InvitationAcceptContent(
        uiState =
        InvitationAcceptUIState(
            isLoading = false,
            orgName = "Acme Corp",
            role = "Staff",
            isUserSignedIn = true,
            isInviteValid = true,
        ),
        onFullNameChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onAcceptClicked = {},
        onSignInClicked = {},
    )
}

@ScreenPreviews
@Composable
internal fun InvitationAcceptErrorPreview() {
    InvitationAcceptContent(
        uiState =
        InvitationAcceptUIState(
            isLoading = false,
            isUserSignedIn = false,
            isInviteValid = false,
            error = "This invitation link is invalid or has expired.",
        ),
        onFullNameChange = {},
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onAcceptClicked = {},
        onSignInClicked = {},
    )
}
