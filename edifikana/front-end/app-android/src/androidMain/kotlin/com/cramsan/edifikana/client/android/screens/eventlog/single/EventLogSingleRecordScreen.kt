package com.cramsan.edifikana.client.android.screens.eventlog.single

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.client.android.utils.shareToWhatsApp
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

@Composable
fun EventLogSingleRecordScreen(
    eventLogRecordPK: EventLogRecordPK,
    viewModel: EventLogSingleRecordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecord(eventLogRecordPK)
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is EventLogSingleRecordUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is EventLogSingleRecordUIState.Success -> {
                EventLogSingleRecord(
                    state.record,
                ) {
                    context.shareToWhatsApp("Compartir", null)
                }
            }
            is EventLogSingleRecordUIState.Error -> {
                // Error
            }
        }
    }
}

@Composable
private fun EventLogSingleRecord(
    eventLogRecord: EventLogRecordUIModel,
    onShareClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = eventLogRecord.summary,
                style = MaterialTheme.typography.titleMedium,
            )
            HorizontalDivider()
            Row {
                Text(
                    text = "Evento: ",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = eventLogRecord.eventType,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            HorizontalDivider()
            Row {
                Text(
                    text = "Fecha y hora: ",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = eventLogRecord.timeRecorded,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            HorizontalDivider()
            Row {
                Text(
                    text = "Dpto: ",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = eventLogRecord.unit,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            HorizontalDivider()
            Text(
                text = eventLogRecord.description,
                style = MaterialTheme.typography.bodyMedium,
            )
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
            Text(text = "Compartir")
            Icon(
                imageVector = Icons.Sharp.Share,
                contentDescription = "compartir",
                modifier = Modifier.padding(4.dp).size(24.dp),
            )
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun ClockInOutEmployeeScreenActionsPreview() {
    EventLogSingleRecord(
        EventLogRecordUIModel(
            summary = "Delivery of pizza",
            eventType = "Invitado",
            timeRecorded = "2024 12 02 12:12:12",
            unit = "302",
            description = "Pizza delivery to the main entrance. The delivery was made by the main entrance. ",
            imageUri = null,
            recordPK = EventLogRecordPK("1"),
        )
    ) { }
}
