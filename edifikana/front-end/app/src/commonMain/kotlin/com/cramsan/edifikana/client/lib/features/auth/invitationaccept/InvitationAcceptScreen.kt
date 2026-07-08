package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaSecondaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.invitation_accept_screen_title
import edifikana_lib.invitation_landing_screen_body
import edifikana_lib.invitation_landing_screen_create_account_button
import edifikana_lib.invitation_landing_screen_sign_in_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Invitation landing screen — presented when the user taps an invitation link and has no active
 * session. Routes to the existing Sign Up / Sign In screens; never collects credentials itself.
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
        onCreateAccountClicked = { viewModel.navigateToSignUp() },
        onSignInClicked = { viewModel.navigateToSignIn() },
    )
}

/**
 * Content of the invitation landing screen.
 */
@Composable
internal fun InvitationAcceptContent(
    uiState: InvitationAcceptUIState,
    modifier: Modifier = Modifier,
    onCreateAccountClicked: () -> Unit,
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
                    text = stringResource(Res.string.invitation_landing_screen_body),
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
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = stringResource(Res.string.invitation_landing_screen_create_account_button),
                    onClick = onCreateAccountClicked,
                    enabled = !uiState.isLoading,
                    modifier = buttonModifier,
                )

                EdifikanaSecondaryButton(
                    text = stringResource(Res.string.invitation_landing_screen_sign_in_button),
                    onClick = onSignInClicked,
                    enabled = !uiState.isLoading,
                    modifier = buttonModifier,
                )
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
        )
    }
}
