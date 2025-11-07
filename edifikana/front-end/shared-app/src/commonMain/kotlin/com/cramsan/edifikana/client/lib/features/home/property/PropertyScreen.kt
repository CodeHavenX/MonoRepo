package com.cramsan.edifikana.client.lib.features.home.property

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.rememberDialogController
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.string_save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Property screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyScreen(
    destination: HomeDestination.PropertyManagementDestination,
    viewModel: PropertyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogController = rememberDialogController()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadContent(destination.propertyId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                PropertyEvent.ShowRemoveDialog -> {
                    dialogController.showDialog(
                        ConfirmRemoveDialog(
                            onConfirmed = {
                                viewModel.removeProperty()
                            }
                        )
                    )
                }
                PropertyEvent.ShowSaveBeforeExitingDialog -> {
                    dialogController.showDialog(
                        ConfirmExitingDialog(
                            onSaveSelected = {
                                viewModel.saveChanges(true)
                            },
                        )
                    )
                }
            }
        }
    }

    PropertyContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onNewEmployeeSelected = { employee ->
            viewModel.addEmployee(employee)
        },
        onEmployeeActionSelected = { employee ->
            viewModel.toggleEmployeeState(employee)
        },
        onSaveChangesSelected = {
            viewModel.saveChanges(false)
        },
        onNewSuggestionsRequested = {
            viewModel.requestNewSuggestions(it)
        },
        onShowRemoveDialogSelected = {
            viewModel.showRemoveDialog()
        },
        onPropertyNameChanged = {
            viewModel.updatePropertyName(it)
        },
        onAddressChanged = {
            viewModel.updatePropertyAddress(it)
        },
    )
    dialogController.Render()
}

/**
 * Content of the AccountEdit screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PropertyContent(
    content: PropertyUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onNewEmployeeSelected: (email: String) -> Unit,
    onEmployeeActionSelected: (employee: EmployeeUIModel) -> Unit,
    onSaveChangesSelected: () -> Unit,
    onPropertyNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onNewSuggestionsRequested: (String) -> Unit,
    onShowRemoveDialogSelected: () -> Unit,
) {
    var newEmployee by remember(content) { mutableStateOf(content.addEmployeeEmail) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.title.orEmpty(),
                onNavigationIconSelected = onBackSelected,
            ) {
                IconButton(onClick = onShowRemoveDialogSelected) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                    )
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
                    OutlinedTextField(
                        value = content.propertyName.orEmpty(),
                        onValueChange = { name ->
                            onPropertyNameChanged(name)
                        },
                        label = { Text("Property name") },
                        modifier = sectionModifier,
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = content.address.orEmpty(),
                        onValueChange = { address ->
                            onAddressChanged(address)
                        },
                        label = { Text("Address") },
                        modifier = sectionModifier,
                        singleLine = true,
                    )

                    HorizontalDivider(sectionModifier)

                    content.employee.forEach { employee ->
                        EmployeeItem(
                            employee.email,
                            employee.isRemoving,
                            sectionModifier,
                        ) {
                            onEmployeeActionSelected(employee)
                        }
                    }

                    OutlinedTextField(
                        value = newEmployee,
                        onValueChange = {
                            newEmployee = it
                            onNewSuggestionsRequested(it)
                        },
                        label = { Text("New Employee's email") },
                        modifier = sectionModifier,
                        singleLine = true,
                        isError = content.addEmployeeError,
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        onNewEmployeeSelected(newEmployee)
                                    }
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onNewEmployeeSelected(newEmployee)
                            }
                        )
                    )
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        onClick = {
                            onSaveChangesSelected()
                        },
                    ) {
                        Text(text = stringResource(Res.string.string_save))
                    }
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}

@Composable
private fun EmployeeItem(
    email: String,
    isRemoving: Boolean,
    modifier: Modifier = Modifier,
    onEmployeeActionSelected: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            email,
            color = if (isRemoving) {
                Color.Gray
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (isRemoving) {
                TextDecoration.LineThrough
            } else {
                null
            },
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (isRemoving) {
                Icons.Filled.Add
            } else {
                Icons.Filled.Close
            },
            contentDescription = "Remove",
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onEmployeeActionSelected() }
        )
    }
}
