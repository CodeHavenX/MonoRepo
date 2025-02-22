package com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.toFriendlyStringCompose
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ScreenLayout
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.ui.components.Dropdown
import com.cramsan.ui.components.LoadingAnimationOverlay
import edifikana_lib.Res
import edifikana_lib.add_record_screen_title
import edifikana_lib.text_add
import edifikana_lib.text_appartment
import edifikana_lib.text_event_type
import edifikana_lib.text_full_desc
import edifikana_lib.text_simple_desc
import edifikana_lib.text_staff
import edifikana_lib.text_staff_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Add Record screen.
 */
@Composable
fun AddRecordScreen(
    viewModel: AddRecordViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(AddRecordEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadStaffs()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            AddRecordEvent.Noop -> Unit
            is AddRecordEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(viewModelEvent.edifikanaApplicationEvent)
            }
        }
    }

    AddRecord(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
    ) { staffDocumentId,
        unit,
        eventType,
        fallbackStaffName,
        fallbackEventType,
        title,
        description ->
        viewModel.addRecord(
            staffDocumentId,
            unit,
            eventType,
            fallbackStaffName,
            fallbackEventType,
            title,
            description,
        )
    }
}

@Composable
internal fun AddRecord(
    uiState: AddRecordUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onAddRecordClicked: (
        staffDocumentId: StaffId?,
        unit: String?,
        eventType: EventLogEventType?,
        fallbackStaffName: String?,
        fallbackEventType: String?,
        title: String?,
        description: String?,
    ) -> Unit,
) {
    val staffs = uiState.records
    var staffPK by remember { mutableStateOf(staffs.firstOrNull()?.staffPK) }
    var eventType by remember { mutableStateOf(EventLogEventType.OTHER) }
    var unit by remember { mutableStateOf("") }
    var fallbackName by remember { mutableStateOf("") }
    var fallbackEventType by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.add_record_screen_title),
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
                    Dropdown(
                        label = stringResource(Res.string.text_staff),
                        items = staffs,
                        itemLabels = staffs.map { it.fullName },
                        modifier = sectionModifier,
                        startValueMatcher = { it.staffPK == staffPK },
                    ) {
                        staffPK = it.staffPK
                    }

                    if (staffPK == null) {
                        OutlinedTextField(
                            value = fallbackName,
                            onValueChange = { fallbackName = it },
                            label = { Text(stringResource(Res.string.text_staff_name)) },
                            modifier = sectionModifier,
                            isError = fallbackName.isBlank(),
                        )
                    }

                    HorizontalDivider(sectionModifier)

                    Dropdown(
                        label = stringResource(Res.string.text_event_type),
                        items = EventLogEventType.entries,
                        itemLabels = EventLogEventType.entries.map { it.toFriendlyStringCompose() },
                        modifier = sectionModifier,
                        startValueMatcher = { it == eventType },
                    ) {
                        eventType = it
                    }

                    if (eventType == EventLogEventType.OTHER) {
                        OutlinedTextField(
                            value = fallbackEventType,
                            onValueChange = { fallbackEventType = it },
                            label = { Text(stringResource(Res.string.text_event_type)) },
                            modifier = sectionModifier,
                            isError = fallbackEventType.isBlank(),
                        )
                    }

                    HorizontalDivider(sectionModifier)

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(stringResource(Res.string.text_appartment)) },
                        modifier = sectionModifier,
                        isError = unit.isBlank(),
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(Res.string.text_simple_desc)) },
                        modifier = sectionModifier,
                        singleLine = true,
                        isError = title.isBlank(),
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(Res.string.text_full_desc)) },
                        modifier = sectionModifier,
                        isError = description.isBlank(),
                    )
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        onClick = {
                            onAddRecordClicked(
                                staffPK,
                                unit,
                                eventType,
                                fallbackName,
                                fallbackEventType,
                                title,
                                description,
                            )
                        },
                    ) {
                        Text(text = stringResource(Res.string.text_add))
                    }
                }
            )
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}
