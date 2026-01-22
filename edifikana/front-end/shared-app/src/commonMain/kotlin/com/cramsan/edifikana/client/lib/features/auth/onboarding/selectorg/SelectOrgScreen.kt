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
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.OptionCard
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
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
        onCreateWorkspaceClicked = { viewModel.createOrganization() },
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
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    ),
                    onClick = null,
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
