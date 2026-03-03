package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.OptionCard
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.create_new_org_screen_title
import edifikana_lib.select_org_screen_create_workspace_description
import edifikana_lib.select_org_screen_create_workspace_title
import edifikana_lib.select_org_screen_heading
import edifikana_lib.select_org_screen_join_team_description
import edifikana_lib.select_org_screen_join_team_title
import edifikana_lib.select_org_screen_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * SelectOrg screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun SelectOrgScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectOrgViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogController = rememberDialogController()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SelectOrgEvent.Noop -> Unit
            SelectOrgEvent.ShowSignOutConfirmation -> {
                val signOutDialog = SignOutConfirmationDialog(
                    onConfirm = { viewModel.confirmSignOut() },
                    onDismiss = { /* User cancelled */ }
                )
                dialogController.showDialog(signOutDialog)
            }
            is SelectOrgEvent.ShowJoinOrgConfirmation -> {
                val joinOrgDialog = JoinOrgConfirmationDialog(
                    onConfirm = { viewModel.acceptInvite(event.inviteId) },
                    onDismiss = { /* User cancelled */ }
                )
                dialogController.showDialog(joinOrgDialog)
            }
        }
    }

    SelectOrgContent(
        uiState = uiState,
        onCreateWorkspaceClicked = { viewModel.createOrganization() },
        onSignOutClicked = { viewModel.requestSignOut() },
        onJoinOrganizationClicked = { inviteId -> viewModel.requestJoinOrganization(inviteId) },
        modifier = modifier,
    )

    dialogController.Render()
}

/**
 * Content of the SelectOrg screen.
 */
@Composable
internal fun SelectOrgContent(
    uiState: SelectOrgUIState,
    onCreateWorkspaceClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    onJoinOrganizationClicked: (
        inviteId: InviteId,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.create_new_org_screen_title),
                onNavigationIconSelected = onSignOutClicked,
            )
        }
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
            sectionContent = { sectionModifier ->
                // Title
                Text(
                    text = stringResource(Res.string.select_org_screen_heading),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = sectionModifier,
                )

                // Subtitle
                Text(
                    text = stringResource(Res.string.select_org_screen_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                if (uiState.inviteList.isNotEmpty()) {
                    uiState.inviteList.forEach { invite ->
                        // Join existing team option
                        OptionCard(
                            title = stringResource(Res.string.select_org_screen_join_team_title),
                            description = invite.description,
                            icon = Icons.Default.Groups,
                            onClick = { onJoinOrganizationClicked(invite.inviteId) },
                            modifier = sectionModifier,
                        )
                    }
                } else {
                    // Join existing team option (disabled, no invites)
                    OptionCard(
                        title = stringResource(Res.string.select_org_screen_join_team_title),
                        description = stringResource(Res.string.select_org_screen_join_team_description),
                        icon = Icons.Default.Groups,
                        colors = CardDefaults.cardColors().copy(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        ),
                        onClick = null,
                        modifier = sectionModifier,
                    )
                }

                // Create new workspace option
                OptionCard(
                    title = stringResource(Res.string.select_org_screen_create_workspace_title),
                    description = stringResource(Res.string.select_org_screen_create_workspace_description),
                    icon = Icons.Default.Domain,
                    onClick = onCreateWorkspaceClicked,
                    modifier = sectionModifier,
                )
            }
        )
    }
}
