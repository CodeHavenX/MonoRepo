package com.cramsan.edifikana.client.lib.features.root.main.timecard

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.model.StaffId
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

/**
 * Represents the UI state of the Time Card screen.
 */
@Composable
fun TimeCardScreen(
    viewModel: TimeCartViewModel = koinInject(),
    mainActivityViewModel: MainActivityViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(TimeCardEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEvents()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            TimeCardEvent.Noop -> Unit
            is TimeCardEvent.TriggerMainActivityEvent -> {
                mainActivityViewModel.executeMainActivityEvent(viewModelEvent.mainActivityEvent)
            }
        }
    }

    EventList(
        isLoading = uiState.isLoading,
        uiState.timeCardEvents,
        onStaffClick = { staffPK ->
            viewModel.navigateToStaff(staffPK)
        },
        onAddEventClick = {
            viewModel.navigateToStaffList()
        },
    )
}

@Composable
private fun EventList(
    isLoading: Boolean,
    events: List<TimeCardUIModel>,
    onStaffClick: (StaffId) -> Unit,
    onAddEventClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
        ) {
            items(events) { staff ->
                StaffItem(
                    staff,
                    Modifier.fillMaxWidth(),
                    onStaffClick,
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
private fun StaffItem(
    staff: TimeCardUIModel,
    modifier: Modifier = Modifier,
    onStaffSelected: (StaffId) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onStaffSelected(staff.staffPK) }
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Row {
            Text(staff.fullName)
            Spacer(modifier = Modifier.weight(1f))
            Text(staff.eventDescription)
        }
        Text(staff.eventTime)
    }
    HorizontalDivider()
}

@Preview
@Composable
private fun TimeCardScreenPreview() {
    EventList(
        isLoading = true,
        events = listOf(
            TimeCardUIModel(
                "Cesar Andres Ramirez Sanchez",
                "Marco salida",
                "2024 02 12 - 03:24:01",
                StaffId("John"),
            ),
            TimeCardUIModel(
                "Antonio",
                "Marco entrada",
                "2024 02 12 - 03:24:01",
                StaffId("Jane"),
            ),
        ),
        onStaffClick = {},
        onAddEventClick = {},
    )
}
