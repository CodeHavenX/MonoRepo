package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.theme.Spacing
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * OrgDetail screen.
 *
 * Displays organization information and management actions for a single organization.
 */
@Composable
fun OrgDetailScreen(
    destination: SettingsDestination.OrganizationDetailDestination,
    viewModel: OrgDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.orgId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            OrgDetailEvent.Noop -> Unit
        }
    }

    OrgDetailContent(
        uiState = uiState,
        onBackSelected = { viewModel.navigateBack() },
        onLeaveOrganizationTapped = { viewModel.onLeaveOrganizationTapped() },
        onTransferOwnershipTapped = { viewModel.onTransferOwnershipTapped(destination.orgId) },
    )

    when (uiState.dialog) {
        OrgDetailDialogState.None -> {
            Unit
        }

        OrgDetailDialogState.ConfirmLeave -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text("Leave ${uiState.orgName}?") },
                text = {
                    Text(
                        "You will lose access to all properties and data in this organization. " +
                            "This cannot be undone.",
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.confirmLeaveOrganization(destination.orgId) },
                    ) { Text("Leave", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDialog() }) { Text("Cancel") }
                },
            )
        }
    }
}

/**
 * Content of the OrgDetail screen.
 */
@Composable
internal fun OrgDetailContent(
    uiState: OrgDetailUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onLeaveOrganizationTapped: () -> Unit,
    onTransferOwnershipTapped: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Organization Details",
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                OrgHeaderCard(
                    orgName = uiState.orgName,
                    isActive = uiState.isActiveOrg,
                    modifier = sectionModifier,
                )

                InfoCard(
                    userRole = uiState.userRole,
                    memberCount = uiState.memberCount,
                    propertyCount = uiState.propertyCount,
                    joinedDate = uiState.joinedDate,
                    modifier = sectionModifier,
                )

                if (uiState.userRole == OrgRole.OWNER) {
                    TransferOwnershipRow(
                        onClick = onTransferOwnershipTapped,
                        modifier = sectionModifier,
                    )
                }

                HorizontalDivider(modifier = sectionModifier)
            },
            buttonContent = { buttonModifier ->
                LeaveButton(
                    isSoleOwner = uiState.isSoleOwner,
                    onClick = onLeaveOrganizationTapped,
                    modifier = buttonModifier,
                )
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
        )
    }
}

@Composable
private fun OrgHeaderCard(
    orgName: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column {
                Text(text = orgName, style = MaterialTheme.typography.headlineSmall)
                if (isActive) {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    userRole: OrgRole?,
    memberCount: Int,
    propertyCount: Int,
    joinedDate: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            InfoRow(
                label = "Your Role",
                value = userRole?.name?.lowercase()?.replaceFirstChar { it.uppercaseChar() } ?: "—",
            )
            InfoRow(label = "Members", value = memberCount.toString())
            InfoRow(label = "Properties", value = propertyCount.toString())
            InfoRow(label = "Joined", value = joinedDate.ifEmpty { "—" })
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TransferOwnershipRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
        modifier
            .fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Transfer Ownership", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Assign a new owner and step down",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun LeaveButton(
    isSoleOwner: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            enabled = !isSoleOwner,
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Leave Organization")
        }
        if (isSoleOwner) {
            Text(
                text = "Transfer ownership before leaving",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}
