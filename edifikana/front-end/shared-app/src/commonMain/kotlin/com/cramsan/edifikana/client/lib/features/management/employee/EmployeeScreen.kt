package com.cramsan.edifikana.client.lib.features.management.employee

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.ui.components.Dropdown
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * Employee screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun EmployeeScreen(
    destination: ManagementDestination.EmployeeDestination,
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadEmployee(destination.employeeId)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                EmployeeEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    EmployeeContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        onSaveClicked = { viewModel.onSaveClicked() },
        onFirstNameChange = { viewModel.updateFirstName(it) },
        onLastNameChange = { viewModel.updateLastName(it) },
        onRoleSelected = { viewModel.updateRole(it) },
        modifier = modifier,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun EmployeeContent(
    content: EmployeeUIState,
    onBackSelected: () -> Unit,
    onSaveClicked: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onRoleSelected: (EmployeeRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.title,
                onNavigationIconSelected = onBackSelected,
            ) {
                if (content.canSave != null) {
                    IconButton(
                        onClick = onSaveClicked,
                        enabled = content.canSave,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    if (content.isEditable == false) {
                        Text(
                            text = "This information is not editable. Ask the user to make changes to their account.",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = sectionModifier,
                        )
                    }

                    OutlinedTextField(
                        value = content.firstName.orEmpty(),
                        onValueChange = {
                            onFirstNameChange(it)
                        },
                        modifier = sectionModifier,
                        label = { Text("First name") },
                        singleLine = true,
                        readOnly = content.isEditable != true,
                    )

                    OutlinedTextField(
                        value = content.lastName.orEmpty(),
                        onValueChange = {
                            onLastNameChange(it)
                        },
                        modifier = sectionModifier,
                        label = { Text("Last name") },
                        singleLine = true,
                        readOnly = content.isEditable != true,
                    )

                    content.role?.let { role ->
                        Dropdown(
                            label = "Role",
                            items = EmployeeRole.entries,
                            itemLabels = EmployeeRole.entries.map { it.name },
                            modifier = sectionModifier,
                            startValueMatcher = { role == it },
                        ) {
                            onRoleSelected(it)
                        }
                    }
                },
                buttonContent = { buttonModifier ->
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
