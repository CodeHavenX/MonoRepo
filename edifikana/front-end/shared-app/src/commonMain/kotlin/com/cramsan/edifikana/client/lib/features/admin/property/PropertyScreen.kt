package com.cramsan.edifikana.client.lib.features.admin.property

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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.admin.AdminDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.string_save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Property screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyScreen(
    destination: AdminDestination.PropertyAdminDestination,
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: PropertyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(PropertyEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadContent(destination.propertyId)
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            PropertyEvent.Noop -> Unit
            is PropertyEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    PropertyContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onNewManagerSelected = { email ->
            viewModel.addManager(email)
        },
        onRemoveManagerSelected = { email ->
            viewModel.removeManager(email)
        },
        onSaveChangesSelected = { name, address ->
            viewModel.saveChanges(name, address)
        }
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyContent(
    content: PropertyUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onNewManagerSelected: (email: String) -> Unit,
    onRemoveManagerSelected: (email: String) -> Unit,
    onSaveChangesSelected: (name: String, address: String) -> Unit,
) {
    var propertyName by remember(content) { mutableStateOf(content.propertyName.orEmpty()) }
    var address by remember(content) { mutableStateOf(content.address.orEmpty()) }
    var newManager by remember(content.addManagerEmail) { mutableStateOf(content.addManagerEmail) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.propertyName.orEmpty(),
                onCloseClicked = onBackSelected,
            )
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
                        value = propertyName,
                        onValueChange = { propertyName = it },
                        label = { Text("Property name") },
                        modifier = sectionModifier,
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = sectionModifier,
                        singleLine = true,
                    )

                    HorizontalDivider(sectionModifier)

                    content.managers.forEach { manager ->
                        ManagerItem(manager, sectionModifier) {
                            onRemoveManagerSelected(manager)
                        }
                    }

                    OutlinedTextField(
                        value = newManager,
                        onValueChange = { newManager = it },
                        label = { Text("New manager's email") },
                        modifier = sectionModifier,
                        singleLine = true,
                        isError = content.addManagerError,
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        onNewManagerSelected(newManager)
                                    }
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onNewManagerSelected(newManager)
                            }
                        )
                    )
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        onClick = {
                            onSaveChangesSelected(propertyName, address)
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
private fun ManagerItem(
    email: String,
    modifier: Modifier = Modifier,
    onRemoveManagerSelected: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(email)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Remove,
            contentDescription = "Remove",
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onRemoveManagerSelected() }
        )
    }
}
