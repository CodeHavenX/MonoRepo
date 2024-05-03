package com.cramsan.edifikana.client.android.screens.clockinout.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cramsan.edifikana.client.android.compose.Dropdown
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.client.android.utils.toIdTypeFriendlyName
import com.cramsan.edifikana.client.android.utils.toRoleFriendlyName
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.IdType

@Composable
fun ClockInOutAddEmployeeScreen(
    navController: NavHostController,
    viewModel: ClockInOutSingleEmployeeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState()

    LaunchedEffect(event) {
        when (event) {
            ClockInOutSingleEmployeeUIEvent.UploadCompleted -> {
                navController.popBackStack()
            }
            ClockInOutSingleEmployeeUIEvent.Noop -> { }
        }
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is ClockInOutSingleEmployeeUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is ClockInOutSingleEmployeeUIState.Success -> {
                ClockInOutAddEmployeeScreenActions { id, idType, name, lastName, role ->
                    viewModel.saveEmployee(id, idType, name, lastName, role)
                }
            }
            is ClockInOutSingleEmployeeUIState.Error -> {
                // Error
            }
        }
    }
}

@Composable
private fun ClockInOutAddEmployeeScreenActions(
    onSaveDataClicked: (
        id: String?,
        idType: IdType?,
        name: String?,
        lastName: String?,
        role: EmployeeRole?, ) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
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
            label = { Text("Documento de identidad") },
            modifier = Modifier.fillMaxWidth(),
        )

        Dropdown(
            label = "Tipo de documento",
            items = IdType.entries,
            itemLabels = IdType.entries.map { it.toIdTypeFriendlyName() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            idType = it
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Dropdown(
            label = "Función",
            items = EmployeeRole.entries,
            itemLabels = EmployeeRole.entries.map { it.toRoleFriendlyName() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            role = it
        }

        Button(onClick = {
            onSaveDataClicked(id, idType, name, lastName, role)
        }) {
            Text("Guardar")
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun ClockInOutEmployeeScreenActionsPreview() {
    ClockInOutAddEmployeeScreenActions(
        onSaveDataClicked = { _, _, _, _, _ -> },
    )
}
