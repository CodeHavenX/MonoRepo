package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.theme.Spacing
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
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

    MyOrganizationsContent(
        uiState = uiState,
        onBackSelected = { viewModel.navigateBack() },
        onOrgSelected = { orgId, isActive ->
            if (isActive) {
                viewModel.onOrgSelected(orgId)
            } else {
                viewModel.requestSwitchOrg(orgId)
            }
        },
        onJoinOrganizationSelected = { /* handled by issue #391 */ },
    )

    when (val dialog = uiState.dialog) {
        MyOrganizationsDialogState.None -> {
            Unit
        }

        is MyOrganizationsDialogState.ConfirmSwitchOrg -> {
            val orgName = uiState.organizations.firstOrNull { it.orgId == dialog.orgId }?.name ?: ""
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text("Switch to $orgName?") },
                text = { Text("You will be switched to this organization's dashboard.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onConfirmSwitchOrg(dialog.orgId) }) { Text("Switch") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDialog() }) { Text("Cancel") }
                },
            )
        }
    }
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
        ScreenLayout(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            fixedFooter = true,
            sectionContent = { sectionModifier ->
                uiState.organizations.forEach { org ->
                    OrgCard(
                        org = org,
                        onClick = { onOrgSelected(org.orgId, org.isActive) },
                        modifier = sectionModifier,
                    )
                }
                HorizontalDivider(modifier = sectionModifier)
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = "+ Join Organization",
                    modifier = buttonModifier,
                    onClick = onJoinOrganizationSelected,
                )
            },
            overlay = {
                LoadingAnimationOverlay(uiState.isLoading)
            },
        )
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
