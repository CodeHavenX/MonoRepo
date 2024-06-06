package com.cramsan.edifikana.client.android.features.eventlog.addrecord

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.toFriendlyStringCompose
import com.cramsan.edifikana.client.lib.ui.components.Dropdown
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventType

@Composable
fun AddRecordScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: AddRecordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(AddRecordEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            AddRecordEvent.Noop -> Unit
            is AddRecordEvent.TriggerMainActivityEvent -> {
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
                label = stringResource(R.string.text_employee),
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
                    label = { Text(stringResource(R.string.text_employee_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fallbackName.isBlank(),
                )
            }

            HorizontalDivider()

            Dropdown(
                label = stringResource(R.string.text_event_type),
                items = EventType.entries,
                itemLabels = EventType.entries.map { it.toFriendlyStringCompose() },
                modifier = Modifier.fillMaxWidth(),
                startValueMatcher = { it == eventType },
            ) {
                eventType = it
            }

            if (eventType == EventType.OTHER) {
                TextField(
                    value = fallbackEventType,
                    onValueChange = { fallbackEventType = it },
                    label = { Text(stringResource(R.string.text_event_type)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fallbackEventType.isBlank(),
                )
            }

            HorizontalDivider()

            TextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text(stringResource(R.string.text_appartment)) },
                modifier = Modifier.fillMaxWidth(),
                isError = unit.isBlank(),
            )

            TextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text(stringResource(R.string.text_simple_desc)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = summary.isBlank(),
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.text_full_desc)) },
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
            Text(text = stringResource(R.string.text_add))
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Preview(
    showBackground = true,
)
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
