package com.cramsan.edifikana.client.lib.features.formlist.records

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun RecordsScreen(
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: RecordsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(RecordsEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecords()
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            MainActivityDelegatedEvent.Noop -> Unit
            else -> Unit
        }
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            RecordsEvent.Noop -> Unit
            is RecordsEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    RecordsContent(
        uiState.content,
        uiState.isLoading,
        onRecordClick = { viewModel.navigateToRecord(it.recordModel) },
    )
}

@Composable
private fun RecordsContent(
    content: List<RecordsUIModel>,
    loading: Boolean,
    onRecordClick: (RecordsUIModel) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(content) { item ->
            RecordsItem(item, onRecordClick)
        }
    }
    LoadingAnimationOverlay(isLoading = loading)
}

@Composable
private fun RecordsItem(
    recordsUIModel: RecordsUIModel,
    onRecordClick: (RecordsUIModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecordClick(recordsUIModel) }
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = recordsUIModel.name,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = recordsUIModel.timeRecorded,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = recordsUIModel.snippet,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
private fun RecordsScreenPreview() {
    RecordsContent(
        content = listOf(
            RecordsUIModel(
                name = "Form 1",
                timeRecorded = "2021-10-10 10:10:10",
                recordModel = FormRecordModel(
                    formRecordPk = FormRecordPK(""),
                    formPk = FormPK(""),
                    name = "Form 1",
                    timeRecorded = 0,
                    fields = emptyList(),
                ),
                snippet = "Field 1: Value 1 Field 2: Value 2 Field 3: Value 3"
            ),
            RecordsUIModel(
                name = "Form 2",
                timeRecorded = "2021-10-10 10:10:10",
                recordModel = FormRecordModel(
                    formRecordPk = FormRecordPK(""),
                    formPk = FormPK(""),
                    name = "Form 1",
                    timeRecorded = 0,
                    fields = emptyList(),
                ),
                snippet = "Field 1: Value 1 Field 2: Value 2 Field 3: Value 3"
            ),
        ),
        loading = false,
        onRecordClick = { },
    )
}
