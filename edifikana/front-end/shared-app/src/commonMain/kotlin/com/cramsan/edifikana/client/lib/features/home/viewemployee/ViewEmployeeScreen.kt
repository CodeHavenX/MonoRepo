package com.cramsan.edifikana.client.lib.features.home.viewemployee

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
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
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.text_clock_in
import edifikana_lib.text_clock_out
import edifikana_lib.text_upload
import io.github.jan.supabase.storage.authenticatedStorageItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * View employee screen.
 */
@Composable
fun ViewEmployeeScreen(
    employeePK: EmployeeId,
    viewModel: ViewEmployeeViewModel = koinViewModel(),
    delegatedEventEmitter: EventEmitter<EdifikanaWindowDelegatedEvent> = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployee(employeePK)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ViewEmployeeEvent.Noop -> Unit
        }
    }

    LaunchedEffect(Unit) {
        delegatedEventEmitter.events.collect { event ->
            when (event) {
                is EdifikanaWindowDelegatedEvent.HandleReceivedImage -> {
                    viewModel.recordClockEvent(event.uri)
                }

                else -> Unit
            }
        }
    }

    ViewEmployeeContent(
        uiState,
        onClockInClick = {
            viewModel.onClockEventSelected(TimeCardEventType.CLOCK_IN)
        },
        onClockOutClick = {
            viewModel.onClockEventSelected(TimeCardEventType.CLOCK_OUT)
        },
        onShareClick = {
            viewModel.share(it)
        },
        onCloseSelected = {
            viewModel.navigateBack()
        },
    )
}

@Composable
internal fun ViewEmployeeContent(
    uiState: ViewEmployeeUIState,
    modifier: Modifier = Modifier,
    onClockInClick: (ViewEmployeeUIModel.EmployeeUIModel) -> Unit,
    onClockOutClick: (ViewEmployeeUIModel.EmployeeUIModel) -> Unit,
    onShareClick: (TimeCardEventId?) -> Unit,
    onCloseSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = uiState.title,
                onNavigationIconSelected = onCloseSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            val employee = uiState.employee
            val records = uiState.records
            ScreenLayout(
                fixedFooter = true,
                sectionContent = { sectionModifier ->
                    Text(
                        text = employee?.fullName.orEmpty(),
                        modifier = sectionModifier,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = employee?.role.orEmpty(),
                        modifier = sectionModifier,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    records.forEach { record ->
                        TimeCardRecordItem(
                            record,
                            modifier = sectionModifier,
                            onShareClick,
                        )
                    }
                },
                buttonContent = { buttonModifier ->
                    Button(
                        enabled = employee != null,
                        onClick = { employee?.let { it1 -> onClockInClick(it1) } },
                        modifier = buttonModifier,
                    ) {
                        Text(text = stringResource(Res.string.text_clock_in))
                    }
                    Button(
                        enabled = employee != null,
                        onClick = { employee?.let { it1 -> onClockOutClick(it1) } },
                        modifier = buttonModifier,
                    ) {
                        Text(text = stringResource(Res.string.text_clock_out))
                    }
                }
            )
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}

@Composable
internal fun TimeCardRecordItem(
    record: ViewEmployeeUIModel.TimeCardRecordUIModel,
    modifier: Modifier = Modifier,
    onShareClick: (TimeCardEventId?) -> Unit,
) {
    val textColor = if (record.clickable) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    ListCell(
        modifier = modifier,
        onSelection = { onShareClick(record.timeCardRecordPK) },
        startSlot = {
            if (!record.clickable) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = stringResource(Res.string.text_upload),
                    modifier = Modifier
                )
            }
        },
        endSlot = {
            record.publicImageUrl?.let {
                AsyncImage(
                    modifier = Modifier.size(64.dp),
                    model = authenticatedStorageItem("time_card_events", it),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        }
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
    }
    HorizontalDivider()
}
