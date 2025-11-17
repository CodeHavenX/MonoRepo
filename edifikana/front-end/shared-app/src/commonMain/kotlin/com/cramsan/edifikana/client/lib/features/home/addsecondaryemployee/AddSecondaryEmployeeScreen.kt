package com.cramsan.edifikana.client.lib.features.home.addsecondaryemployee

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.toIdTypeFriendlyName
import com.cramsan.edifikana.client.lib.toRoleFriendlyNameCompose
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.Dropdown
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.add_employee_screen_title
import edifikana_lib.string_id_number
import edifikana_lib.string_id_type
import edifikana_lib.string_last_names
import edifikana_lib.string_names
import edifikana_lib.string_role
import edifikana_lib.string_save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * AddSecondary screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun AddSecondaryEmployeeScreen(
    modifier: Modifier = Modifier,
    viewModel: AddSecondaryEmployeeViewModel = koinViewModel(),
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
            AddSecondaryEmployeeEvent.Noop -> Unit
        }
    }

    // Render the screen
    AddSecondaryContent(
        uiState,
        onBackSelected = {
            viewModel.onBackSelected()
        },
        modifier,
    ) { id, idType, name, lastName, role ->
        viewModel.saveEmployee(id, idType, name, lastName, role)
    }
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun AddSecondaryContent(
    content: AddSecondaryEmployeeUIState,
    onBackSelected: () -> Unit,
    modifier: Modifier = Modifier,
    onSaveDataClicked: (
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: EmployeeRole?,
    ) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.add_employee_screen_title),
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
            var id by remember { mutableStateOf("") }
            var idType by remember { mutableStateOf(IdType.DNI) }
            var name by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            var role by remember { mutableStateOf(EmployeeRole.SECURITY) }
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        modifier = sectionModifier,
                        label = { Text(stringResource(Res.string.string_id_number)) },
                        isError = id.isEmpty(),
                        singleLine = true,
                    )

                    Dropdown(
                        label = stringResource(Res.string.string_id_type),
                        items = IdType.entries,
                        modifier = sectionModifier,
                        itemLabels = IdType.entries.map { it.toIdTypeFriendlyName() },
                        startValueMatcher = { it == idType },
                    ) {
                        idType = it
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = sectionModifier,
                        label = { Text(stringResource(Res.string.string_names)) },
                        singleLine = true,
                        isError = name.isEmpty(),
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = sectionModifier,
                        label = { Text(stringResource(Res.string.string_last_names)) },
                        singleLine = true,
                        isError = lastName.isEmpty()
                    )

                    Dropdown(
                        label = stringResource(Res.string.string_role),
                        items = EmployeeRole.entries,
                        modifier = sectionModifier,
                        itemLabels = EmployeeRole.entries.map { it.toRoleFriendlyNameCompose() },
                        startValueMatcher = { it == role },
                    ) {
                        role = it
                    }
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        onClick = {
                            onSaveDataClicked(id, idType, name, lastName, role)
                        }
                    ) {
                        Text(stringResource(Res.string.string_save))
                    }
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
