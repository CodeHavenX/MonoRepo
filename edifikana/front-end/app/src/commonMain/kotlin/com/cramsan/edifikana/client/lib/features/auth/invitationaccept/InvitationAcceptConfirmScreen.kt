package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.invitation_accept_screen_title
import edifikana_lib.invitation_confirm_screen_accept_button
import edifikana_lib.invitation_confirm_screen_body
import edifikana_lib.invitation_confirm_screen_decline_button
import edifikana_lib.invitation_confirm_screen_summary_fallback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Invitation accept/decline screen — presented once an authenticated session exists, whether
 * that session was already active when the deep link opened, or was just established via
 * Sign Up + OTP verification or Sign In from [InvitationAcceptScreen].
 */
@Composable
fun InvitationAcceptConfirmScreen(
    destination: AuthDestination.InvitationAcceptConfirmDestination,
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

    InvitationAcceptConfirmContent(
        uiState = uiState,
        onAcceptClicked = { viewModel.acceptInvitation(destination.inviteId) },
        onDeclineClicked = { viewModel.declineInvitation(destination.inviteId) },
    )
}

/**
 * Content of the invitation accept/decline screen.
 */
@Composable
internal fun InvitationAcceptConfirmContent(
    uiState: InvitationAcceptUIState,
    modifier: Modifier = Modifier,
    onAcceptClicked: () -> Unit,
    onDeclineClicked: () -> Unit,
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
                Card(modifier = sectionModifier.fillMaxWidth()) {
                    Text(
                        text =
                        uiState.invitationSummary
                            ?: stringResource(Res.string.invitation_confirm_screen_summary_fallback),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(Padding.MEDIUM),
                    )
                }

                Text(
                    text = stringResource(Res.string.invitation_confirm_screen_body),
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
                    text = stringResource(Res.string.invitation_confirm_screen_accept_button),
                    onClick = onAcceptClicked,
                    enabled = !uiState.isLoading,
                    modifier = buttonModifier,
                )

                EdifikanaSecondaryButton(
                    text = stringResource(Res.string.invitation_confirm_screen_decline_button),
                    onClick = onDeclineClicked,
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
