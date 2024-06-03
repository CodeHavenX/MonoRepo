package com.cramsan.edifikana.client.android.features.formlist.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
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
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.FormPK

@Composable
fun EntryScreen(
    formPK: FormPK,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(EntryEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadForm(formPK)
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            MainActivityDelegatedEvent.Noop -> Unit
            else -> Unit
        }
    }

    LaunchedEffect(event) {
        when (val event = event) {
            EntryEvent.Noop -> Unit
            is EntryEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(event.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    EntryContent(
        uiState.content,
        uiState.isLoading,
        onValueChange = { fieldId: String, value: String ->
            viewModel.updateField(fieldId, value)
        },
        onAddRecordClick = {
            viewModel.addRecord()
        },
    )
}

@Composable
private fun EntryContent(
    content: EntryUIModel,
    loading: Boolean,
    onValueChange: (fieldId: String, value: String) -> Unit,
    onAddRecordClick: () -> Unit,
) {
    Column {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
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
                EntryFieldItem(field, onValueChange)
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = content.submitAllowed,
            onClick = onAddRecordClick,
        ) {
            Text(text = stringResource(R.string.text_add))
        }
    }
    LoadingAnimationOverlay(isLoading = loading)
}

@Composable
private fun EntryFieldItem(
    field: EntryFieldUIModel,
    onValueChange: (fieldId: String, value: String) -> Unit,
) {
    var value by remember { mutableStateOf("") }

    val isError = value.isBlank() && field.isRequired
    TextField(
        value = value,
        onValueChange = {
            value = it
            onValueChange(field.fieldId, it)
        },
        label = { Text(field.name) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        singleLine = field.isSingleLine,
    )
}

@Preview(
    showBackground = true,
)
@Composable
private fun EntryScreenPreview() {
    EntryContent(
        content = EntryUIModel(
            name = "Registro de Conserjería",
            fields = listOf(
                EntryFieldUIModel(
                    fieldId = "1",
                    name = "Departamento",
                    isRequired = true,
                    isSingleLine = true,
                ),
                EntryFieldUIModel(
                    fieldId = "2",
                    name = "Conserje",
                    isRequired = false,
                    isSingleLine = false,
                ),
                EntryFieldUIModel(
                    fieldId = "2",
                    name = "Observaciones",
                    isRequired = false,
                    isSingleLine = false,
                ),
            ),
            submitAllowed = false,
        ),
        loading = false,
        onValueChange = { _, _ -> },
        onAddRecordClick = { },
    )
}
