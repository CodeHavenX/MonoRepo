package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.theme.Spacing
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.viewmodel.koinViewModel

/**
 * MyOrganizations screen.
 *
 * Displays all organizations the current user belongs to and allows switching between them.
 */
@Composable
fun MyOrganizationsScreen(
    viewModel: MyOrganizationsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MyOrganizationsEvent.Noop -> Unit
        }
    }

    var pendingSwitchOrgId by remember { mutableStateOf<OrganizationId?>(null) }

    pendingSwitchOrgId?.let { orgId ->
        val orgName = uiState.organizations.firstOrNull { it.orgId == orgId }?.name ?: ""
        AlertDialog(
            onDismissRequest = { pendingSwitchOrgId = null },
            title = { Text("Switch to $orgName?") },
            text = { Text("You will be switched to this organization's dashboard.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onConfirmSwitchOrg(orgId)
                    pendingSwitchOrgId = null
                }) { Text("Switch") }
            },
            dismissButton = {
                TextButton(onClick = { pendingSwitchOrgId = null }) { Text("Cancel") }
            },
        )
    }

    MyOrganizationsContent(
        uiState = uiState,
        onBackSelected = { viewModel.navigateBack() },
        onOrgSelected = { orgId, isActive ->
            if (isActive) {
                viewModel.onOrgSelected(orgId)
            } else {
                pendingSwitchOrgId = orgId
            }
        },
        onJoinOrganizationSelected = { /* handled by issue #391 */ },
    )
}

/**
 * Content of the MyOrganizations screen.
 */
@Composable
internal fun MyOrganizationsContent(
    uiState: MyOrganizationsUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onOrgSelected: (OrganizationId, Boolean) -> Unit,
    onJoinOrganizationSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "My Organizations",
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
            LazyColumn(
                modifier =
                    Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.organizations) { org ->
                    OrgCard(
                        org = org,
                        onClick = { onOrgSelected(org.orgId, org.isActive) },
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))

            EdifikanaPrimaryButton(
                text = "+ Join Organization",
                modifier =
                    Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                    onClick = onJoinOrganizationSelected,
            )
        }

        LoadingAnimationOverlay(uiState.isLoading)
    }
}

@Composable
private fun OrgCard(
    org: OrgListItemUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    Text(
                        text = org.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (org.isActive) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Text(
                    text = org.roleLabel,
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
