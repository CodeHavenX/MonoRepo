package com.cramsan.edifikana.client.android.screens.eventlog.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.cramsan.edifikana.client.android.compose.Dropdown
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.client.android.screens.eventlog.add.EventLogAddRecordUIState.EmployeeUIModel
import com.cramsan.edifikana.client.android.screens.eventlog.single.toFriendlyString
import com.cramsan.edifikana.client.android.utils.shareToWhatsApp
import com.cramsan.edifikana.client.android.utils.toIdTypeFriendlyName
import com.cramsan.edifikana.client.android.utils.toRoleFriendlyName
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EmployeeRole
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventType
import com.cramsan.edifikana.lib.firestore.IdType
import java.time.format.DateTimeFormatter
import kotlinx.datetime.Clock

@Composable
fun EventLogSingleAddRecordScreen(
    navController: NavHostController,
    viewModel: EventLogAddRecordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    LaunchedEffect(event) {
        when (event) {
            is EventLogAddRecordUIEvent.OnAddCompleted -> {
                navController.popBackStack()
            }
            EventLogAddRecordUIEvent.Noop -> Unit
        }
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is EventLogAddRecordUIState.Error -> {
                // Error
            }
            EventLogAddRecordUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is EventLogAddRecordUIState.Success -> {
                EventLogAddRecord(
                    state.employees,
                ) { employeeDocumentId,
                    unit,
                    eventType,
                    fallbackEmployeeName,
                    fallbackEventType,
                    summary,
                    description ->
                    viewModel.addRecord(
                        employeeDocumentId,
                        unit,
                        eventType,
                        fallbackEmployeeName,
                        fallbackEventType,
                        summary,
                        description,
                    )
                }
            }
        }
    }
}

@Composable
private fun EventLogAddRecord(
    employees: List<EmployeeUIModel>,
    onAddRecordClicked: (
        employeeDocumentId: EmployeePK?,
        unit: String?,
        eventType: EventType?,
        fallbackEmployeeName: String?,
        fallbackEventType: String?,
        summary: String?,
        description: String?,
    ) -> Unit,
) {
    var employeePK by remember { mutableStateOf(employees.firstOrNull()?.employeePK) }
    var eventType by remember { mutableStateOf(EventType.OTHER) }
    var unit by remember { mutableStateOf("") }
    var fallbackName by remember { mutableStateOf("") }
    var fallbackEventType by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Dropdown(
                label = "Empleado",
                items = employees,
                itemLabels = employees.map { it.fullName },
                modifier = Modifier.fillMaxWidth(),
            ) {
                employeePK = it.employeePK
            }

            if (employeePK == null) {
                TextField(
                    value = fallbackName,
                    onValueChange = { fallbackName = it },
                    label = { Text("Nombre del conserje") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            HorizontalDivider()

            Dropdown(
                label = "Tipo de evento",
                items = EventType.entries,
                itemLabels = EventType.entries.map { it.toFriendlyString() },
                modifier = Modifier.fillMaxWidth(),
                startValueMatcher = { it == eventType },
            ) {
                eventType = it
            }

            if (eventType == EventType.OTHER) {
                TextField(
                    value = fallbackEventType,
                    onValueChange = { fallbackEventType = it },
                    label = { Text("Tipo de evento") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            HorizontalDivider()

            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Departamento") },
                modifier = Modifier.fillMaxWidth(),
            )

            TextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Descripcion simple") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripcion detallada") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                onAddRecordClicked(
                    employeePK,
                    unit,
                    eventType,
                    fallbackName,
                    fallbackEventType,
                    summary,
                    description,
                )
            },
        ) {
            Text(text = "Agregar")
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun ClockInOutEmployeeScreenActionsPreview() {
    EventLogAddRecord(
        listOf(
            EmployeeUIModel(
                "Juan Perez",
                EmployeePK("1"),
            ),
            EmployeeUIModel(
                "Maria Rodriguez",
                null,
            ),
        ),
    ) {_, _, _, _, _, _, _ -> }
}
