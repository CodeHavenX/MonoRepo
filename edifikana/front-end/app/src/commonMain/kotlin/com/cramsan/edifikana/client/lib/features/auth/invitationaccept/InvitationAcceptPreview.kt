package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

@DevicePreviews
@Composable
internal fun InvitationAcceptLandingPreview() =
    AppTheme {
        InvitationAcceptContent(
            uiState =
            InvitationAcceptUIState(
                isLoading = false,
            ),
            onCreateAccountClicked = {},
            onSignInClicked = {},
        )
    }

@ScreenPreviews
@Composable
internal fun InvitationAcceptLandingErrorPreview() =
    AppTheme {
        InvitationAcceptContent(
            uiState =
            InvitationAcceptUIState(
                isLoading = false,
                error = "This invitation link is invalid or has expired.",
            ),
            onCreateAccountClicked = {},
            onSignInClicked = {},
        )
    }

@DevicePreviews
@Composable
internal fun InvitationAcceptConfirmPreview() =
    AppTheme {
        InvitationAcceptConfirmContent(
            uiState =
            InvitationAcceptUIState(
                isLoading = false,
                isUserSignedIn = true,
                invitationSummary = "You have been invited to join Sunrise Property Management.",
            ),
            onAcceptClicked = {},
            onDeclineClicked = {},
        )
    }

@ScreenPreviews
@Composable
internal fun InvitationAcceptConfirmNoSummaryPreview() =
    AppTheme {
        InvitationAcceptConfirmContent(
            uiState =
            InvitationAcceptUIState(
                isLoading = false,
                isUserSignedIn = true,
                invitationSummary = null,
            ),
            onAcceptClicked = {},
            onDeclineClicked = {},
        )
    }

@ScreenPreviews
@Composable
internal fun InvitationAcceptConfirmErrorPreview() =
    AppTheme {
        InvitationAcceptConfirmContent(
            uiState =
            InvitationAcceptUIState(
                isLoading = false,
                isUserSignedIn = true,
                error = "This invitation link is invalid or has expired.",
            ),
            onAcceptClicked = {},
            onDeclineClicked = {},
        )
    }
