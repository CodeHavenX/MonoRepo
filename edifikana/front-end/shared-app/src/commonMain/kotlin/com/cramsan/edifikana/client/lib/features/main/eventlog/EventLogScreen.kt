package com.cramsan.edifikana.client.lib.features.main.eventlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.event_log_screen_add_record
import edifikana_lib.text_upload
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Event Log screen.
 */
@Composable
fun EventLogScreen(
    modifier: Modifier,
    viewModel: EventLogViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(EventLogEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecords()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            EventLogEvent.Noop -> Unit
            is EventLogEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(viewModelEvent.edifikanaApplicationEvent)
            }
        }
    }

    RecordList(
        uiState.records,
        uiState.isLoading,
        modifier = modifier,
        onRecordSelected = {
            viewModel.openRecordScreen(it)
        },
        onAddRecordClicked = {
            viewModel.openAddRecordScreen()
        },
    )
}

@Composable
internal fun RecordList(
    records: List<EventLogRecordUIModel>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onRecordSelected: (EventLogEntryId?) -> Unit,
    onAddRecordClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        ScreenLayout(
            fixedFooter = true,
            maxWith = Dp.Unspecified,
            sectionContent = { sectionModifier ->
                records.forEach { record ->
                    RecordItem(record, sectionModifier, onRecordSelected)
                }
            },
            buttonContent = { buttonModifier ->
                Button(
                    modifier = buttonModifier,
                    onClick = onAddRecordClicked,
                ) {
                    Text(text = stringResource(Res.string.event_log_screen_add_record))
                }
            }
        )
        LoadingAnimationOverlay(isLoading)
    }
}

@Composable
private fun RecordItem(
    record: EventLogRecordUIModel,
    modifier: Modifier = Modifier,
    onRecordSelected: (EventLogEntryId?) -> Unit,
) {
    ListCell(
        modifier = modifier,
        onSelection = { onRecordSelected(record.recordPK) },
        endSlot = {
            if (!record.clickable) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = stringResource(Res.string.text_upload),
                    modifier = Modifier
                )
            }
        },
        content = {
            val textColor = if (record.clickable) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    record.title,
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
        }
    )
    HorizontalDivider()
}
