package com.cramsan.edifikana.client.lib.features.management.viewrecord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.string_field_date_time
import edifikana_lib.string_field_event
import edifikana_lib.string_field_unit
import edifikana_lib.string_gallery
import edifikana_lib.string_share
import edifikana_lib.view_record_screen_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the View Record screen.
 */
@Composable
fun ViewRecordScreen(
    eventLogRecordPK: EventLogEntryId,
    viewModel: ViewRecordViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecord(eventLogRecordPK)
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    ViewRecordEvent.Noop -> Unit
                }
            }
        }
    }

    SingleRecord(
        uiState,
        onShareClicked = { viewModel.share() },
        onPickMultipleVisualMediaClicked = { viewModel.pickMultipleVisualMedia() },
        onImageClicked = { viewModel.openImage(it) },
        onCloseSelected = { viewModel.navigateBack() },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SingleRecord(
    uiState: ViewRecordUIState,
    modifier: Modifier = Modifier,
    onShareClicked: () -> Unit,
    onPickMultipleVisualMediaClicked: () -> Unit,
    onImageClicked: (AttachmentHolder) -> Unit,
    onCloseSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.view_record_screen_title),
                onNavigationIconSelected = onCloseSelected,
            )
        },
    ) { innerPadding ->
        val eventLogRecord = uiState.record
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                fixedFooter = true,
                sectionContent = { sectionModifier ->
                    Text(
                        text = eventLogRecord?.title.orEmpty(),
                        modifier = sectionModifier,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    HorizontalDivider(sectionModifier)
                    Row(
                        modifier = sectionModifier,
                    ) {
                        Text(
                            text = stringResource(Res.string.string_field_event),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = eventLogRecord?.eventType.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    HorizontalDivider(sectionModifier)
                    Row(
                        modifier = sectionModifier,
                    ) {
                        Text(
                            text = stringResource(Res.string.string_field_date_time),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = eventLogRecord?.timeRecorded.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    HorizontalDivider(sectionModifier)
                    Row(modifier = sectionModifier) {
                        Text(
                            text = stringResource(Res.string.string_field_unit),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = eventLogRecord?.unit.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    HorizontalDivider(sectionModifier)
                    Text(
                        text = eventLogRecord?.description.orEmpty(),
                        modifier = sectionModifier,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (eventLogRecord?.attachments?.isNotEmpty() == true) {
                        HorizontalDivider(sectionModifier)
                        FlowRow(
                            modifier = sectionModifier,
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
                },
                buttonContent = { buttonModifier ->
                    Button(
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        enabled = !uiState.isLoading,
                        onClick = {
                            onPickMultipleVisualMediaClicked()
                        },
                    ) {
                        Text(text = stringResource(Res.string.string_gallery))
                        Icon(
                            imageVector = Icons.Sharp.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(34.dp),
                        )
                    }
                    Button(
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        enabled = !uiState.isLoading,
                        onClick = {
                            onShareClicked()
                        },
                    ) {
                        Text(text = stringResource(Res.string.string_share))
                        Icon(
                            imageVector = Icons.Sharp.Share,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(24.dp),
                        )
                    }
                },
            )
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}

private const val COLUMNS = 4
