package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.viewmodel.koinViewModel

/**
 * TransferOwnership screen.
 *
 * Displays eligible admin members to whom ownership can be transferred.
 */
@Composable
fun TransferOwnershipScreen(
    destination: SettingsDestination.TransferOwnershipDestination,
    viewModel: TransferOwnershipViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.orgId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            TransferOwnershipEvent.Noop -> Unit
        }
    }

    uiState.confirmingTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            title = { Text("Transfer ownership to ${target.displayName}?") },
            text = { Text("You will become an Admin. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmTransferOwnership(destination.orgId) },
                ) { Text("Transfer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDialog() }) { Text("Cancel") }
            },
        )
    }

    TransferOwnershipContent(
        uiState = uiState,
        onBackSelected = { viewModel.navigateBack() },
        onAdminSelected = { admin -> viewModel.onAdminSelected(admin) },
    )
}

/**
 * Content of the TransferOwnership screen.
 */
@Composable
internal fun TransferOwnershipContent(
    uiState: TransferOwnershipUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onAdminSelected: (AdminUIModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Transfer Ownership",
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                .padding(innerPadding)
                .fillMaxSize(),
                ) {
            if (!uiState.isLoading && uiState.eligibleAdmins.isEmpty()) {
                Text(
                    text = "No eligible admins. Promote a Staff member to Admin first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(Spacing.lg),
                )
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(uiState.eligibleAdmins) { admin ->
                        AdminCard(
                            admin = admin,
                            onClick = { onAdminSelected(admin) },
                        )
                    }
                }
            }
        }

        LoadingAnimationOverlay(uiState.isLoading)
    }
}

@Composable
private fun AdminCard(
    admin: AdminUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(text = admin.displayName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = admin.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
