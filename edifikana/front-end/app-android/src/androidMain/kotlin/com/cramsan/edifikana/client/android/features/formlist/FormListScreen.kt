package com.cramsan.edifikana.client.android.features.formlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.main.MainActivityDelegatedEvent
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.FormPK

@Composable
fun FormListScreen(
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: FormListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(FormListEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadForms()
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            MainActivityDelegatedEvent.Noop -> Unit
            else -> Unit
        }
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            FormListEvent.Noop -> Unit
            is FormListEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    FormList(
        uiState.forms,
        uiState.isLoading,
        onFormSelected = { viewModel.navigateToForm(it) },
        onFormEntriesSelected = { viewModel.navigateToFormRecords() },
    )
}

@Composable
private fun FormList(
    records: List<FormUIModel>,
    loading: Boolean,
    onFormSelected: (FormUIModel) -> Unit,
    onFormEntriesSelected: () -> Unit,
) {
    Column {
        FormEntriesItem(onFormEntriesSelected)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(records) { record ->
                FormListItem(record, onFormSelected)
            }
        }
    }
    LoadingAnimationOverlay(isLoading = loading)
}

@Composable
private fun FormEntriesItem(onFormEntriesSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFormEntriesSelected() }
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.text_view_records))
    }
    HorizontalDivider(
        thickness = 3.dp,
    )
}

@Composable
private fun FormListItem(record: FormUIModel, onFormSelected: (FormUIModel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFormSelected(record) }
            .padding(16.dp)
    ) {
        Text(text = record.name)
    }
    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Composable
fun FormListScreenPreview() {
    FormList(
        records = listOf(
            FormUIModel(name = "Form 1", formPk = FormPK("documentId1")),
            FormUIModel(name = "Form 2", formPk = FormPK("documentId2")),
            FormUIModel(name = "Form 3", formPk = FormPK("documentId3")),
        ),
        loading = false,
        onFormSelected = { },
        onFormEntriesSelected = { },
    )
}
