package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.account_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * CreateNewOrg screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun CreateNewOrgScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateNewOrgViewModel = koinViewModel(),
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
                OrganizationDescriptionField(
                    value = content.organizationDescription,
                    onValueChange = onOrganizationDescriptionChanged,
                    modifier = sectionModifier,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaPrimaryButton(
                    text = "Create Organization",
                    onClick = onCreateOrganizationClicked,
                    modifier = buttonModifier,
                )
            }
        )
    }
}

/**
 * Multiline text field for organization description.
 */
@Composable
private fun OrganizationDescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Organization Description",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = {
                Text(
                    text = "Briefly describe what this workspace is for...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
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
