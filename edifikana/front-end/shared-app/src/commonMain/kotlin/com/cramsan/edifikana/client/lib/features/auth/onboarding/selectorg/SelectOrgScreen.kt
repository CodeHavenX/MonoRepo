package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
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
    val dialogController = rememberDialogController()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadContent()
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
        }
    }

    SelectOrgContent(
        onJoinTeamClicked = { viewModel.joinTeam() },
        onCreateWorkspaceClicked = { viewModel.createWorkspace() },
        onSignOutClicked = { viewModel.requestSignOut() },
        modifier = modifier,
    )

    dialogController.Render()
}

/**
 * Content of the SelectOrg screen.
 */
@Composable
internal fun SelectOrgContent(
    onJoinTeamClicked: () -> Unit,
    onCreateWorkspaceClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Get Started",
                onNavigationIconSelected = onSignOutClicked,
            )
        }
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                // Title
                Text(
                    text = "How do you want to start?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = sectionModifier,
                )

                // Subtitle
                Text(
                    text = "Choose how you want to manage your properties.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                // Join existing team option
                OptionCard(
                    title = "Join an existing team",
                    description = "I have an invite code or want to search for my company.",
                    icon = Icons.Default.Groups,
                    onClick = onJoinTeamClicked,
                    modifier = sectionModifier,
                )

                // Create new workspace option
                OptionCard(
                    title = "Create a new workspace",
                    description = "I want to set up a new property portfolio for my team.",
                    icon = Icons.Default.Domain,
                    onClick = onCreateWorkspaceClicked,
                    modifier = sectionModifier,
                )
            }
        )
    }
}

@Composable
private fun OptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(Padding.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.size(Padding.X_SMALL))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.size(Padding.MEDIUM))

            // Icon with circular background
            Box(
                modifier = Modifier
                    .size(Size.xx_large)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Size.large),
                )
            }
        }
    }
}
