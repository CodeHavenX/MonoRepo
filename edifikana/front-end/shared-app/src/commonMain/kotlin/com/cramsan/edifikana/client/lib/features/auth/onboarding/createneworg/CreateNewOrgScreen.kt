package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * CreateNewOrg screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun CreateNewOrgScreen(modifier: Modifier = Modifier, viewModel: CreateNewOrgViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // For other possible lifecycle events, see the Lifecycle.Event documentation.
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            CreateNewOrgEvent.Noop -> Unit
        }
    }

    // Render the screen
    CreateNewOrgContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        onOrganizationNameChanged = { viewModel.onOrganizationNameChanged(it) },
        onOrganizationDescriptionChanged = { viewModel.onOrganizationDescriptionChanged(it) },
        onCreateOrganizationClicked = { viewModel.onCreateOrganizationClicked() },
        modifier = modifier,
    )
}

/**
 * Content of the CreateNewOrg screen.
 */
@Composable
internal fun CreateNewOrgContent(
    content: CreateNewOrgUIState,
    onBackSelected: () -> Unit,
    onOrganizationNameChanged: (String) -> Unit,
    onOrganizationDescriptionChanged: (String) -> Unit,
    onCreateOrganizationClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Get Started",
                onNavigationIconSelected = onBackSelected,
            )
        },
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
                    text = "Create New Organization",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = sectionModifier,
                )

                // Subtitle
                Text(
                    text = "Enter the details for your new team workspace.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                // Organization Name field
                EdifikanaTextField(
                    value = content.organizationName,
                    onValueChange = onOrganizationNameChanged,
                    label = "Organization Name",
                    placeholder = "e.g. Acme Properties",
                    modifier = sectionModifier,
                )

                // Organization Description field (multiline)
                EdifikanaTextField(
                    value = content.organizationDescription,
                    onValueChange = onOrganizationDescriptionChanged,
                    label = "Organization Description",
                    placeholder = "Briefly describe what this workspace is for...",
                    modifier = sectionModifier,
                    minLines = 4,
                    maxLines = 5,
                    singleLine = false,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = "Create Organization",
                    onClick = onCreateOrganizationClicked,
                    modifier = buttonModifier,
                    enabled = content.isButtonEnabled,
                )
            },
        )
    }
}
