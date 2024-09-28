package com.cramsan.edifikana.client.lib.features.timecard.addemployee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.toIdTypeFriendlyName
import com.cramsan.edifikana.client.lib.toRoleFriendlyNameCompose
import com.cramsan.edifikana.client.lib.ui.components.Dropdown
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import edifikana_lib.Res
import edifikana_lib.string_id_number
import edifikana_lib.string_id_type
import edifikana_lib.string_last_names
import edifikana_lib.string_names
import edifikana_lib.string_role
import edifikana_lib.string_save
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun AddEmployeeScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: AddEmployeeViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(AddEmployeeEvent.Noop)

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            AddEmployeeEvent.Noop -> Unit
            is AddEmployeeEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    AddEmployeeForm(uiState.isLoading) { id, idType, name, lastName, role ->
        viewModel.saveEmployee(id, idType, name, lastName, role)
    }
}

@Composable
private fun AddEmployeeForm(
    isLoading: Boolean,
    onSaveDataClicked: (
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: EmployeeRole?,
    ) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var id by remember { mutableStateOf("") }
        var idType by remember { mutableStateOf(IdType.DNI) }
        var name by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var role by remember { mutableStateOf(EmployeeRole.SECURITY) }

        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(stringResource(Res.string.string_id_number)) },
            modifier = Modifier.fillMaxWidth(),
            isError = id.isEmpty()
        )

        Dropdown(
            label = stringResource(Res.string.string_id_type),
            items = IdType.entries,
            itemLabels = IdType.entries.map { it.toIdTypeFriendlyName() },
            modifier = Modifier.fillMaxWidth(),
            startValueMatcher = { it == idType },
        ) {
            idType = it
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(Res.string.string_names)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = name.isEmpty()
        )

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(stringResource(Res.string.string_last_names)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = lastName.isEmpty()
        )

        Dropdown(
            label = stringResource(Res.string.string_role),
            items = EmployeeRole.entries,
            itemLabels = EmployeeRole.entries.map { it.toRoleFriendlyNameCompose() },
            modifier = Modifier.fillMaxWidth(),
            startValueMatcher = { it == role },
        ) {
            role = it
        }

        Button(onClick = {
            onSaveDataClicked(id, idType, name, lastName, role)
        }) {
            Text(stringResource(Res.string.string_save))
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Preview
@Composable
private fun AddEmployeeScreenPreview() {
    AddEmployeeForm(
        isLoading = true,
        onSaveDataClicked = { _, _, _, _, _ -> },
    )
}
