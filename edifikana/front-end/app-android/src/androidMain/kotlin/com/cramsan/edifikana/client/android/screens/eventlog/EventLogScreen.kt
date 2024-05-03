package com.cramsan.edifikana.client.android.screens.eventlog

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.cramsan.edifikana.client.android.Screens
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

@Composable
fun EventLogScreen(
    navController: NavHostController,
    viewModel: EventLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecords()
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is EventLogUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is EventLogUIState.Empty -> {
                RecordList(
                    emptyList(),
                    onRecordSelected = {},
                    onAddRecordClicked = {
                        navController.navigate(Screens.EventLogAddItem.route)
                    },
                )
            }
            is EventLogUIState.Success -> {
                RecordList(
                    state.records,
                    onRecordSelected = {
                        navController.navigate("eventlog/${it.documentPath}")
                    },
                    onAddRecordClicked = {
                        navController.navigate(Screens.EventLogAddItem.route)
                    },
                )
            }
            is EventLogUIState.Error -> {
                // Error
            }
        }
    }
}

@Composable
private fun RecordList(
    records: List<EventLogRecordUIModel>,
    onRecordSelected: (EventLogRecordPK) -> Unit,
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
                .padding(16.dp)
            ,
            onClick = onAddRecordClicked,
        ) {
            Text(text = "Agregar")
        }
    }
}

@Composable
private fun RecordItem(
    record: EventLogRecordUIModel,
    onRecordSelected: (EventLogRecordPK) -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable { onRecordSelected(record.recordPK) }
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(record.summary, style = MaterialTheme.typography.bodyLarge)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(record.timeRecorded, style = MaterialTheme.typography.labelMedium)
            Text(record.eventType, style = MaterialTheme.typography.labelMedium)
            Text(record.unit, style = MaterialTheme.typography.labelMedium)
        }
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewClockInOutScreen() {
    RecordList(
        records = listOf(
            EventLogRecordUIModel(
                "Arrived package for dpt 1801",
                "DELIVERY",
                "1801",
                "2021-09-01T00:00:00Z",
                EventLogRecordPK("1"),
            ),
        ),
        onRecordSelected = { },
        onAddRecordClicked = { },
    )
}