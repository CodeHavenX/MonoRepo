package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPasswordTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.invitation_accept_screen_accept_button
import edifikana_lib.invitation_accept_screen_confirm_password_label
import edifikana_lib.invitation_accept_screen_create_password_label
import edifikana_lib.invitation_accept_screen_email_label
import edifikana_lib.invitation_accept_screen_existing_user_subtitle
import edifikana_lib.invitation_accept_screen_full_name_label
import edifikana_lib.invitation_accept_screen_new_user_subtitle
import edifikana_lib.invitation_accept_screen_sign_in_link
import edifikana_lib.invitation_accept_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Invitation accept screen — presented when the user taps an invitation link.
 *
 * Shows the new-user registration form when the user is not signed in, or a simplified
 * accept-only view when a session already exists.
 */
@Composable
fun InvitationAcceptScreen(
    destination: AuthDestination.InvitationAcceptDestination,
    viewModel: InvitationAcceptViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(destination.inviteId) {
        viewModel.loadInvitation(destination.inviteId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            InvitationAcceptEvent.Noop -> Unit
        }
    }

    InvitationAcceptContent(
        uiState = uiState,
        onFullNameChange = { viewModel.updateFullName(it) },
        onPasswordChange = { viewModel.updatePassword(it) },
        onConfirmPasswordChange = { viewModel.updateConfirmPassword(it) },
        onAcceptClicked = {
            if (uiState.isUserSignedIn) {
                viewModel.acceptAsExistingUser(destination.inviteId)
            } else {
                viewModel.acceptAsNewUser(destination.inviteId)
            }
        },
        onSignInClicked = { viewModel.navigateToSignIn() },
    )
}

/**
 * Content of the invitation accept screen.
 */
@Composable
internal fun InvitationAcceptContent(
    uiState: InvitationAcceptUIState,
    modifier: Modifier = Modifier,
    onFullNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAcceptClicked: () -> Unit,
    onSignInClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(title = stringResource(Res.string.invitation_accept_screen_title))
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                Text(
                    text =
                    if (uiState.isUserSignedIn) {
                        stringResource(Res.string.invitation_accept_screen_existing_user_subtitle)
                    } else {
                        stringResource(Res.string.invitation_accept_screen_new_user_subtitle)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                uiState.error?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = sectionModifier,
                    )
                }

                if (!uiState.isUserSignedIn) {
                    EdifikanaTextField(
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        label = stringResource(Res.string.invitation_accept_screen_full_name_label),
                        modifier = sectionModifier,
                    )

                    EdifikanaTextField(
                        value = uiState.inviteEmail,
                        onValueChange = {},
                        label = stringResource(Res.string.invitation_accept_screen_email_label),
                        enabled = false,
                        modifier = sectionModifier,
                    )

                    EdifikanaPasswordTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = stringResource(Res.string.invitation_accept_screen_create_password_label),
                        modifier = sectionModifier,
                    )

                    EdifikanaPasswordTextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = stringResource(Res.string.invitation_accept_screen_confirm_password_label),
                        modifier = sectionModifier,
                    )
                }
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.invitation_accept_screen_accept_button),
                    onClick = onAcceptClicked,
                    enabled = !uiState.isLoading,
                    modifier = buttonModifier,
                )

                if (!uiState.isUserSignedIn) {
                    TextButton(
                        onClick = onSignInClicked,
                        modifier = buttonModifier,
                    ) {
                        Text(stringResource(Res.string.invitation_accept_screen_sign_in_link))
                    }
                }
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
        )
    }
}
