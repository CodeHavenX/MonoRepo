package com.cramsan.edifikana.client.lib.features.timecard.viewstaff

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.edifikana.client.lib.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import edifikana_lib.Res
import edifikana_lib.text_clock_in
import edifikana_lib.text_clock_out
import edifikana_lib.text_upload
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

/**
 * View staff screen.
 */
@Composable
fun ViewStaffScreen(
    staffPK: StaffPK,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: ViewStaffViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(ViewStaffEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadStaff(staffPK)
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is ViewStaffEvent.Noop -> { }
            is ViewStaffEvent.TriggerMainActivityEvent -> {
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
    ViewStaffContent(
        uiState.isLoading,
        uiState.staff,
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
private fun ViewStaffContent(
    isLoading: Boolean,
    staff: ViewStaffUIModel.StaffUIModel?,
    records: List<ViewStaffUIModel.TimeCardRecordUIModel>,
    onClockInClick: (ViewStaffUIModel.StaffUIModel) -> Unit,
    onClockOutClick: (ViewStaffUIModel.StaffUIModel) -> Unit,
    onShareClick: (TimeCardRecordPK?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (staff != null) {
            Text(
                text = staff.fullName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = staff.role,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = { onClockInClick(staff) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.text_clock_in))
                }
                Button(
                    onClick = { onClockOutClick(staff) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(Res.string.text_clock_out))
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
    record: ViewStaffUIModel.TimeCardRecordUIModel,
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
                contentDescription = stringResource(Res.string.text_upload),
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

@Preview
@Composable
private fun ViewStaffScreenPreview() {
    // TODO: Move to a centralized place in core-compose
    AssertUtil.setInstance(NoopAssertUtil)

    ViewStaffContent(
        isLoading = true,
        staff = ViewStaffUIModel.StaffUIModel(
            fullName = "Cesar Andres Ramirez Sanchez",
            role = "Descansero",
            staffPK = StaffPK("123"),
        ),
        records = listOf(
            ViewStaffUIModel.TimeCardRecordUIModel(
                eventType = "Entrada",
                timeRecorded = "2021-01-01 12:00:00",
                StorageRef("storage/1"),
                TimeCardEventType.CLOCK_IN,
                null,
                TimeCardRecordPK("123-123-123"),
                true,
            ),
            ViewStaffUIModel.TimeCardRecordUIModel(
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
