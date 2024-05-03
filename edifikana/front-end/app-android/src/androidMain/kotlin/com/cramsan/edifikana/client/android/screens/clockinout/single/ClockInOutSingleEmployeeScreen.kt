package com.cramsan.edifikana.client.android.screens.clockinout.single

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.cramsan.edifikana.client.android.MainActivityEvents
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.client.android.utils.shareToWhatsApp
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType

@Composable
fun ClockInOutSingleEmployeeScreen(
    navController: NavHostController,
    employeePK: EmployeePK,
    mainActivityEvents: MainActivityEvents,
    onCameraRequested: (String) -> Unit = {},
    viewModel: ClockInOutSingleEmployeeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState()
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployee(employeePK)
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is ClockInOutSingleEmployeeUIEvent.OnAddRecordRequested -> {
                onCameraRequested(localEvent.filename)
            }
            is ClockInOutSingleEmployeeUIEvent.Noop -> { }
            is ClockInOutSingleEmployeeUIEvent.ShareEvent -> {
                context.shareToWhatsApp(localEvent.text, localEvent.imageUri)
            }
        }
    }

    LaunchedEffect(mainActivityEvents) {
        when (mainActivityEvents) {
            is MainActivityEvents.OnCameraComplete -> {
                viewModel.recordClockEvent(employeePK, mainActivityEvents.photoUri)
            }
            is MainActivityEvents.Noop -> Unit
            is MainActivityEvents.LaunchSignIn -> Unit
            is MainActivityEvents.ShareToWhatsApp -> Unit
        }
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is ClockInOutSingleEmployeeUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is ClockInOutSingleEmployeeUIState.Empty -> {
                // Empty
            }
            is ClockInOutSingleEmployeeUIState.Success -> {
                ClockInOutEmployeeScreenActions(
                    state.employee,
                    state.records,
                    onClockInClicked = {
                        viewModel.onClockEventSelected(TimeCardEventType.CLOCK_IN)
                    },
                    onClockOutClicked = {
                        viewModel.onClockEventSelected(TimeCardEventType.CLOCK_OUT)
                    },
                    onShareClicked = {
                        viewModel.share()
                    },
                )
            }
            is ClockInOutSingleEmployeeUIState.Error -> {
                // Error
            }
        }
    }
}

@Composable
private fun ClockInOutEmployeeScreenActions(
    employee: EmployeeUIModel,
    records: List<TimeCardRecordUIModel>,
    onClockInClicked: (EmployeeUIModel) -> Unit,
    onClockOutClicked: (EmployeeUIModel) -> Unit,
    onShareClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = employee.fullName,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = employee.role,
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                onClick = { onClockInClicked(employee) },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = "Marcar entrada")
            }
            Button(
                onClick = { onClockOutClicked(employee) },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = "Marcar salída")
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(records) { record ->
                TimeCardRecordItem(record)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = {
                onShareClicked()
            },
        ) {
            Text(text = "Compartir ultimo registro")
            Icon(
                imageVector = Icons.Sharp.Share,
                contentDescription = "Compartir ultimo registro",
                modifier = Modifier.padding(4.dp).size(24.dp),
            )
        }
    }
}

@Composable
private fun TimeCardRecordItem(record: TimeCardRecordUIModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = record.eventType)
        Text(text = record.timeRecorded)
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
private fun ClockInOutEmployeeScreenActionsPreview() {
    ClockInOutEmployeeScreenActions(
        employee = EmployeeUIModel(
            fullName = "Cesar Andres Ramirez Sanchez",
            role = "Descansero",
            employeePK = EmployeePK("123"),
        ),
        records = listOf(
            TimeCardRecordUIModel(
                eventType = "Entrada",
                timeRecorded = "2021-01-01 12:00:00",
                "",
                TimeCardEventType.CLOCK_IN,
            ),
            TimeCardRecordUIModel(
                eventType = "Salida",
                timeRecorded = "2021-01-01 12:00:00",
                "",
                TimeCardEventType.CLOCK_OUT,
            ),
        ),
        onClockInClicked = {},
        onClockOutClicked = {},
        onShareClicked = {},
    )
}
