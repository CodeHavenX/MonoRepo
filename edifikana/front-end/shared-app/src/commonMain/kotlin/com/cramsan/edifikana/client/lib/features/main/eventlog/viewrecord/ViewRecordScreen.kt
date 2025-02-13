package com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.PhotoLibrary
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationDelegatedEvent
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.ui.components.LoadingAnimationOverlay
import edifikana_lib.Res
import edifikana_lib.string_field_date_time
import edifikana_lib.string_field_event
import edifikana_lib.string_field_unit
import edifikana_lib.string_gallery
import edifikana_lib.string_share
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the View Record screen.
 */
@Composable
fun ViewRecordScreen(
    eventLogRecordPK: EventLogEntryId,
    viewModel: ViewRecordViewModel = koinViewModel(),
    edifikanaApplicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(ViewRecordEvent.Noop)
    val mainActivityDelegatedEvent by edifikanaApplicationViewModel.delegatedEvents.collectAsState(
        EdifikanaApplicationDelegatedEvent.Noop
    )

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecord(eventLogRecordPK)
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            ViewRecordEvent.Noop -> Unit
            is ViewRecordEvent.TriggerEdifikanaApplicationEvent -> {
                edifikanaApplicationViewModel.executeEvent(viewModelEvent.edifikanaApplicationEvent)
            }
        }
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (val delegatedEvent = mainActivityDelegatedEvent) {
            EdifikanaApplicationDelegatedEvent.Noop -> Unit
            is EdifikanaApplicationDelegatedEvent.HandleReceivedImages -> {
                viewModel.upload(delegatedEvent.uris)
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "View Record",
                onCloseClicked = { viewModel.navigateBack() },
            )
        },
    ) { innerPadding ->
        SingleRecord(
            uiState.isLoading,
            Modifier.padding(innerPadding),
            uiState.record,
            onShareClicked = { viewModel.share() },
            onPickMultipleVisualMediaClicked = { viewModel.pickMultipleVisualMedia() },
            onImageClicked = { viewModel.openImage(it) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SingleRecord(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    eventLogRecord: ViewRecordUIModel?,
    onShareClicked: () -> Unit,
    onPickMultipleVisualMediaClicked: () -> Unit,
    onImageClicked: (AttachmentHolder) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
    ) {
        eventLogRecord?.let {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = eventLogRecord.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                HorizontalDivider()
                Row {
                    Text(
                        text = stringResource(Res.string.string_field_event),
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
                        text = stringResource(Res.string.string_field_date_time),
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
                        text = stringResource(Res.string.string_field_unit),
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
                if (eventLogRecord.attachments.isNotEmpty()) {
                    HorizontalDivider()
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        maxItemsInEachRow = COLUMNS,
                    ) {
                        val itemModifier = Modifier
                            .padding(4.dp)
                            .height(80.dp)
                            .weight(1f)
                        eventLogRecord.attachments.forEach {
                            AsyncImage(
                                modifier = itemModifier.clickable {
                                    onImageClicked(it)
                                },
                                model = it.publicUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    enabled = !isLoading,
                    onClick = {
                        onPickMultipleVisualMediaClicked()
                    },
                ) {
                    Text(text = stringResource(Res.string.string_gallery))
                    Icon(
                        imageVector = Icons.Sharp.PhotoLibrary,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(34.dp),
                    )
                }
            }
            HorizontalDivider()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                enabled = !isLoading,
                onClick = {
                    onShareClicked()
                },
            ) {
                Text(text = stringResource(Res.string.string_share))
                Icon(
                    imageVector = Icons.Sharp.Share,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp),
                )
            }
        }
    }
    LoadingAnimationOverlay(isLoading)
}

private const val COLUMNS = 4
