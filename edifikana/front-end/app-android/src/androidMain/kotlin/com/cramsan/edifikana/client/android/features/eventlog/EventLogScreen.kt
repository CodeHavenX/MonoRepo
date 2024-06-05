package com.cramsan.edifikana.client.android.features.eventlog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

@Composable
fun EventLogScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: EventLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(EventLogEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecords()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            EventLogEvent.Noop -> Unit
            is EventLogEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    RecordList(
        uiState.records,
        uiState.isLoading,
        onRecordSelected = {
            viewModel.openRecordScreen(it)
        },
        onAddRecordClicked = {
            viewModel.openAddRecordScreen()
        },
    )
}

@Composable
private fun RecordList(
    records: List<EventLogRecordUIModel>,
    isLoading: Boolean,
    onRecordSelected: (EventLogRecordPK?) -> Unit,
    onAddRecordClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(records) { record ->
                RecordItem(record, onRecordSelected)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onAddRecordClicked,
        ) {
            Text(text = "Agregar")
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Composable
private fun RecordItem(
    record: EventLogRecordUIModel,
    onRecordSelected: (EventLogRecordPK?) -> Unit,
) {
    val textColor = if (record.clickable) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    Row(
        modifier = Modifier
            .clickable { onRecordSelected(record.recordPK) }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                record.summary,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    record.timeRecorded,
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    record.eventType,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                )
                Text(
                    record.unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                )
            }
        }
        if (!record.clickable) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = stringResource(R.string.text_upload),
                modifier = Modifier
            )
        }
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewEventLogScreen() {
    RecordList(
        records = listOf(
            EventLogRecordUIModel(
                "Arrived package for dpt 1801",
                "DELIVERY",
                "1801",
                "2021-09-01T00:00:00Z",
                EventLogRecordPK("1"),
                true,
            ),
            EventLogRecordUIModel(
                "Arrived package for dpt 1801",
                "DELIVERY",
                "1801",
                "2021-09-01T00:00:00Z",
                EventLogRecordPK("1"),
                false,
            ),
        ),
        isLoading = true,
        onRecordSelected = { },
        onAddRecordClicked = { },
    )
}
