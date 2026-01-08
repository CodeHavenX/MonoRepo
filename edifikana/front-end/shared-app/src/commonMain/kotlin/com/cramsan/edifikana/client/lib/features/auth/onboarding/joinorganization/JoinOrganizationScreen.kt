package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * JoinOrganization screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun JoinOrganizationScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinOrganizationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            JoinOrganizationEvent.Noop -> Unit
        }
    }

    // Render the screen
    JoinOrganizationContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        onOrganizationNameOrCodeChanged = { viewModel.onOrganizationNameOrCodeChanged(it) },
        onJoinOrganizationClicked = { viewModel.onJoinOrganizationClicked() },
        onCreateNewWorkspaceClicked = { viewModel.onCreateNewWorkspaceClicked() },
        modifier = modifier,
    )
}

/**
 * Content of the JoinOrganization screen.
 */
@Composable
internal fun JoinOrganizationContent(
    content: JoinOrganizationUIState,
    onBackSelected: () -> Unit,
    onOrganizationNameOrCodeChanged: (String) -> Unit,
    onJoinOrganizationClicked: () -> Unit,
    onCreateNewWorkspaceClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Get Started",
                onNavigationIconSelected = onBackSelected,
            )
        }
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            fixedFooter = true,
            overlay = {
                LoadingAnimationOverlay(content.isLoading)
            },
            sectionContent = { sectionModifier ->
                // Title
                Text(
                    text = "Find your team",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = sectionModifier,
                )

                // Subtitle
                Text(
                    text = "Enter the organization's unique name or the invite code shared with you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                // Organization Name or Invite Code field with search icon
                OrganizationSearchField(
                    value = content.organizationNameOrCode,
                    onValueChange = onOrganizationNameOrCodeChanged,
                    modifier = sectionModifier,
                )

                // Helper text
                Text(
                    text = "The name is usually the company name without spaces.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )
            },
            buttonContent = { buttonModifier ->
                // Join Organization button
                EdifikanaPrimaryButton(
                    text = "Join Organization",
                    onClick = onJoinOrganizationClicked,
                    modifier = buttonModifier,
                )

                // Don't have a code text
                Text(
                    text = "Don't have a code?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = buttonModifier,
                )

                // Create new workspace link
                Text(
                    text = "Create a new workspace instead",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = buttonModifier
                        .clickable(onClick = onCreateNewWorkspaceClicked),
                )
            }
        )
    }
}

/**
 * Text field with search icon for organization name or invite code.
 */
@Composable
private fun OrganizationSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Organization Name or Invite Code",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "e.g. acme-properties or X892A",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
        )
    }
}
