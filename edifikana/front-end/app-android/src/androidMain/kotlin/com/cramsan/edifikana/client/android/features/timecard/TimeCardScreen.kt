package com.cramsan.edifikana.client.android.features.timecard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeePK

@Composable
fun TimeCardScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: TimeCartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(TimeCardEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEvents()
    }

    LaunchedEffect(event) {
        when (val event = event) {
            TimeCardEvent.Noop -> Unit
            is TimeCardEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(event.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    EventList(
        isLoading = uiState.isLoading,
        uiState.timeCardEvents,
        onEmployeeClick = { employeePK ->
            viewModel.navigateToEmployee(employeePK)
        },
        onAddEventClick = {
            viewModel.navigateToEmployeeList()
        },
    )
}

@Composable
private fun EventList(
    isLoading: Boolean,
    events: List<TimeCardUIModel>,
    onEmployeeClick: (EmployeePK) -> Unit,
    onAddEventClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
        ) {
            items(events) { employee ->
                EmployeeItem(
                    employee,
                    Modifier.fillMaxWidth(),
                    onEmployeeClick,
                )
            }
        }
        FloatingActionButton(
            onClick = onAddEventClick,
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd),
        ) {
            Icon(Icons.Filled.Add, "")
        }
        LoadingAnimationOverlay(isLoading)
    }
}

@Composable
private fun EmployeeItem(
    employee: TimeCardUIModel,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (EmployeePK) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onEmployeeSelected(employee.employeePK) }
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Row {
            Text(employee.fullName)
            Spacer(modifier = Modifier.weight(1f))
            Text(employee.eventDescription)
        }
        Text(employee.eventTime)
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
private fun TimeCardScreenPreview() {
    EventList(
        isLoading = true,
        events = listOf(
            TimeCardUIModel("Cesar Andres Ramirez Sanchez", "Marco salida", "2024 02 12 - 03:24:01", EmployeePK("John")),
            TimeCardUIModel("Antonio", "Marco entrada", "2024 02 12 - 03:24:01", EmployeePK("Jane")),
        ),
        onEmployeeClick = {},
        onAddEventClick = {},
    )
}
