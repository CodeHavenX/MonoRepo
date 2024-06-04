package com.cramsan.edifikana.client.android.features.formlist.records.read

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.FormRecordPK

@Composable
fun RecordReadScreen(
    formRecordPK: FormRecordPK,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: RecordReadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(RecordReadEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRecord(formRecordPK)
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            MainActivityDelegatedEvent.Noop -> Unit
            else -> Unit
        }
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            RecordReadEvent.Noop -> Unit
            is RecordReadEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    RecordReadContent(
        uiState.content,
        uiState.isLoading,
    )
}

@Composable
private fun RecordReadContent(content: RecordReadUIModel, loading: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = content.name,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        items(content.fields) { field ->
            RecordFieldItem(field)
        }
    }
    LoadingAnimationOverlay(isLoading = loading)
}

@Composable
private fun RecordFieldItem(field: RecordFieldUIModel) {
    Text(
        text = field.name,
        style = MaterialTheme.typography.titleMedium,
    )
    Text(
        text = field.value,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun RecordReadScreenPreview() {
    RecordReadContent(
        content = RecordReadUIModel(
            "Test Form",
            listOf(
                RecordFieldUIModel("Field 1", "Value 1"),
                RecordFieldUIModel("Field 2", "Value 2"),
                RecordFieldUIModel("Field 3", "Value 3"),
            )
        ),
        loading = false,
    )
}
