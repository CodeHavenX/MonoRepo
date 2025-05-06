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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.string_save
import kotlinx.coroutines.launch
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
    destination: ManagementDestination.PropertyManagementDestination,
    viewModel: PropertyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadContent(destination.propertyId)
    }

    val screenScope = rememberCoroutineScope()
    screenScope.launch {
        viewModel.events.collect { event ->
            when (event) {
                PropertyEvent.Noop -> Unit
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
        },
        onSuggestionSelected = {
            viewModel.selectSuggestion(it)
        },
        onNewSuggestionsRequested = {
            viewModel.requestNewSuggestions(it)
        }
    )
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
    onNewManagerSelected: (email: String) -> Unit,
    onRemoveManagerSelected: (email: String) -> Unit,
    onSaveChangesSelected: (name: String, address: String) -> Unit,
    onSuggestionSelected: (suggestion: String) -> Unit,
    onNewSuggestionsRequested: (String) -> Unit,
) {
    var propertyName by remember(content) { mutableStateOf(content.propertyName.orEmpty()) }
    var address by remember(content) { mutableStateOf(content.address.orEmpty()) }
    var newManager by remember(content) { mutableStateOf(content.addManagerEmail) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.propertyName.orEmpty(),
                onNavigationIconSelected = onBackSelected,
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

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                    ) {
                        LaunchedEffect(content.suggestions) {
                            if (content.suggestions.isNotEmpty()) {
                                expanded = true
                            }
                        }

                        /**
                         * There is a bug in the Material dropdown menu that will cause focus to be stuck when the
                         * dropdown is opened.
                         * https://github.com/JetBrains/compose-multiplatform/issues/4782
                         * https://issuetracker.google.com/issues/369748464
                         */

                        OutlinedTextField(
                            value = newManager,
                            onValueChange = {
                                newManager = it
                                onNewSuggestionsRequested(it)
                            },
                            label = { Text("New manager's email") },
                            modifier = sectionModifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = false),
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
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            content.suggestions.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        expanded = false
                                        onSuggestionSelected(s)
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
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
