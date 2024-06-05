package com.cramsan.edifikana.client.android.features.timecard.viewemployee

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.AsyncImage
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil

@Composable
fun ViewEmployeeScreen(
    employeePK: EmployeePK,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: ViewEmployeeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(ViewEmployeeEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployee(employeePK)
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is ViewEmployeeEvent.Noop -> { }
            is ViewEmployeeEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(localEvent.mainActivityEvent)
            }
        }
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            is MainActivityDelegatedEvent.HandleReceivedImage -> {
                viewModel.recordClockEvent(mainActivityDelegatedEvent.uri)
            }
            else -> Unit
        }
    }

    onTitleChange(uiState.title)
    ViewEmployeeContent(
        uiState.isLoading,
        uiState.employee,
        uiState.records,
        onClockInClick = {
            viewModel.onClockEventSelected(TimeCardEventType.CLOCK_IN)
        },
        onClockOutClick = {
            viewModel.onClockEventSelected(TimeCardEventType.CLOCK_OUT)
        },
        onShareClick = {
            viewModel.share(it)
        },
    )
}

@Composable
private fun ViewEmployeeContent(
    isLoading: Boolean,
    employee: ViewEmployeeUIModel.EmployeeUIModel?,
    records: List<ViewEmployeeUIModel.TimeCardRecordUIModel>,
    onClockInClick: (ViewEmployeeUIModel.EmployeeUIModel) -> Unit,
    onClockOutClick: (ViewEmployeeUIModel.EmployeeUIModel) -> Unit,
    onShareClick: (TimeCardRecordPK?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (employee != null) {
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
                    onClick = { onClockInClick(employee) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.text_clock_in))
                }
                Button(
                    onClick = { onClockOutClick(employee) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.text_clock_out))
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(records) { record ->
                TimeCardRecordItem(record, onShareClick)
            }
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Composable
private fun TimeCardRecordItem(
    record: ViewEmployeeUIModel.TimeCardRecordUIModel,
    onShareClick: (TimeCardRecordPK?) -> Unit,
) {
    val textColor = if (record.clickable) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShareClick(record.timeCardRecordPK) }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = record.eventType,
                color = textColor,
            )
            Text(
                text = record.timeRecorded,
                color = textColor,
            )
        }
        if (!record.clickable) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = stringResource(R.string.text_upload),
                modifier = Modifier
            )
        }
        AsyncImage(
            modifier = Modifier.size(64.dp),
            model = record.publicImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
private fun ViewEmployeeScreenPreview() {
    // TODO: Move to a centralized place in core-compose
    AssertUtil.setInstance(NoopAssertUtil)

    ViewEmployeeContent(
        isLoading = true,
        employee = ViewEmployeeUIModel.EmployeeUIModel(
            fullName = "Cesar Andres Ramirez Sanchez",
            role = "Descansero",
            employeePK = EmployeePK("123"),
        ),
        records = listOf(
            ViewEmployeeUIModel.TimeCardRecordUIModel(
                eventType = "Entrada",
                timeRecorded = "2021-01-01 12:00:00",
                StorageRef("storage/1"),
                TimeCardEventType.CLOCK_IN,
                null,
                TimeCardRecordPK("123-123-123"),
                true,
            ),
            ViewEmployeeUIModel.TimeCardRecordUIModel(
                eventType = "Salida",
                timeRecorded = "2021-01-01 12:00:00",
                StorageRef("storage/2"),
                TimeCardEventType.CLOCK_OUT,
                null,
                TimeCardRecordPK("321"),
                false,
            ),
        ),
        onClockInClick = {},
        onClockOutClick = {},
        onShareClick = {},
    )
}
