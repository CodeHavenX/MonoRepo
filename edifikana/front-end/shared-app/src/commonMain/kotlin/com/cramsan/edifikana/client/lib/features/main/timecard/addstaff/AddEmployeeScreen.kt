package com.cramsan.edifikana.client.lib.features.main.timecard.addstaff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.toIdTypeFriendlyName
import com.cramsan.edifikana.client.lib.toRoleFriendlyNameCompose
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.ui.components.Dropdown
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.add_staff_screen_title
import edifikana_lib.string_id_number
import edifikana_lib.string_id_type
import edifikana_lib.string_last_names
import edifikana_lib.string_names
import edifikana_lib.string_role
import edifikana_lib.string_save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Add staff screen.
 */
@Composable
fun AddStaffScreen(
    viewModel: AddStaffViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(AddStaffEvent.Noop)

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            AddStaffEvent.Noop -> Unit
            is AddStaffEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(viewModelEvent.edifikanaApplicationEvent)
            }
        }
    }

    AddStaffForm(
        uiState,
        onBackSelected = { viewModel.navigateBack() }
    ) { id, idType, name, lastName, role ->
        viewModel.saveStaff(id, idType, name, lastName, role)
    }
}

@Composable
internal fun AddStaffForm(
    uiState: AddStaffUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onSaveDataClicked: (
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: StaffRole?,
    ) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.add_staff_screen_title),
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
            var id by remember { mutableStateOf("") }
            var idType by remember { mutableStateOf(IdType.DNI) }
            var name by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            var role by remember { mutableStateOf(StaffRole.SECURITY) }
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
                        items = StaffRole.entries,
                        modifier = sectionModifier,
                        itemLabels = StaffRole.entries.map { it.toRoleFriendlyNameCompose() },
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
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}
