package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.toFriendlyStringCompose
import com.cramsan.edifikana.client.lib.ui.components.Dropdown
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.model.EventLogEventType
import edifikana_lib.Res
import edifikana_lib.text_add
import edifikana_lib.text_appartment
import edifikana_lib.text_employee
import edifikana_lib.text_employee_name
import edifikana_lib.text_event_type
import edifikana_lib.text_full_desc
import edifikana_lib.text_simple_desc
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun AddRecordScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: AddRecordViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(
        com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordEvent.Noop
    )

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordEvent.Noop -> Unit
            is com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    AddRecord(
        uiState.employees,
        uiState.isLoading,
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

@Composable
private fun AddRecord(
    employees: List<AddRecordUIModel>,
    isLoading: Boolean,
    onAddRecordClicked: (
        employeeDocumentId: EmployeePK?,
        unit: String?,
        eventType: EventLogEventType?,
        fallbackEmployeeName: String?,
        fallbackEventType: String?,
        summary: String?,
        description: String?,
    ) -> Unit,
) {
    var employeePK by remember { mutableStateOf(employees.firstOrNull()?.employeePK) }
    var eventType by remember { mutableStateOf(EventLogEventType.OTHER) }
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
                label = stringResource(Res.string.text_employee),
                items = employees,
                itemLabels = employees.map { it.fullName },
                modifier = Modifier.fillMaxWidth(),
                startValueMatcher = { it.employeePK == employeePK },
            ) {
                employeePK = it.employeePK
            }

            if (employeePK == null) {
                TextField(
                    value = fallbackName,
                    onValueChange = { fallbackName = it },
                    label = { Text(stringResource(Res.string.text_employee_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fallbackName.isBlank(),
                )
            }

            HorizontalDivider()

            Dropdown(
                label = stringResource(Res.string.text_event_type),
                items = EventLogEventType.entries,
                itemLabels = EventLogEventType.entries.map { it.toFriendlyStringCompose() },
                modifier = Modifier.fillMaxWidth(),
                startValueMatcher = { it == eventType },
            ) {
                eventType = it
            }

            if (eventType == EventLogEventType.OTHER) {
                TextField(
                    value = fallbackEventType,
                    onValueChange = { fallbackEventType = it },
                    label = { Text(stringResource(Res.string.text_event_type)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fallbackEventType.isBlank(),
                )
            }

            HorizontalDivider()

            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text(stringResource(Res.string.text_appartment)) },
                modifier = Modifier.fillMaxWidth(),
                isError = unit.isBlank(),
            )

            TextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text(stringResource(Res.string.text_simple_desc)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = summary.isBlank(),
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(Res.string.text_full_desc)) },
                modifier = Modifier.fillMaxWidth(),
                isError = description.isBlank(),
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
            Text(text = stringResource(Res.string.text_add))
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Preview
@Composable
private fun AddRecordPreview() {
    AddRecord(
        listOf(
            AddRecordUIModel(
                "Juan Perez",
                EmployeePK("1"),
            ),
            AddRecordUIModel(
                "Maria Rodriguez",
                null,
            ),
        ),
        true,
    ) { _, _, _, _, _, _, _ -> }
}
