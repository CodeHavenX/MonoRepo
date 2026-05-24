package com.cramsan.architecture.client.features.debugsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Entry-point composable for the debug settings screen.
 *
 * Renders all registered settings grouped by domain and sub-group. If the current build is
 * not a debug build, shows a placeholder message instead of real settings.
 *
 * @param viewModel The ViewModel driving this screen; resolved via Koin by default.
 * @param onBack Callback invoked when the user taps the back button in the top app bar.
 * @param modifier Optional [Modifier] applied to the root [Scaffold].
 */
@Composable
fun DebugSettingsScreen(
    viewModel: DebugSettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadSettings()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            DebugSettingsEvent.Noop -> {
                Unit
            }

            is DebugSettingsEvent.ShowSnackbar -> {
                scope.launch { snackbarHostState.showSnackbar(event.message) }
            }
        }
    }

    DebugSettingsContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSaveBoolean = { key, value -> viewModel.saveValue(key, value) },
        onSaveString = { key, value -> viewModel.saveValue(key, value) },
        onBack = onBack,
        modifier = modifier,
    )
}

/**
 * Stateless content composable for the debug settings screen.
 *
 * @param uiState Current UI state.
 * @param snackbarHostState State object controlling snackbar visibility.
 * @param onSaveBoolean Called when a boolean setting is toggled.
 * @param onSaveString Called when a text setting's focus is lost with a new value.
 * @param onBack Callback for the top-app-bar back button.
 * @param modifier Optional [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DebugSettingsContent(
    uiState: DebugSettingsUIState,
    snackbarHostState: SnackbarHostState,
    onSaveBoolean: (SettingKey<*>, Boolean) -> Unit,
    onSaveString: (SettingKey<*>, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Debug Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (!uiState.isDebugBuild) {
            Column(
                modifier =
                    Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                    ) {
                Text(
                    text = "Debug settings are not available in release builds.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                    ) {
                uiState.groups.forEach { group ->
                    item {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                    group.subGroups.forEach { subGroup ->
                        item {
                            Text(
                                text = subGroup.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                        }
                        items(subGroup.rows) { row ->
                            SettingRowItem(
                                row = row,
                                onSaveBoolean = onSaveBoolean,
                                onSaveString = onSaveString,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRowItem(
    row: SettingRowUIModel,
    onSaveBoolean: (SettingKey<*>, Boolean) -> Unit,
    onSaveString: (SettingKey<*>, String) -> Unit,
) {
    when (row) {
        is SettingRowUIModel.BooleanRow -> {
            BooleanSettingRow(row, onSaveBoolean)
        }

        is SettingRowUIModel.StringRow -> {
            TextSettingRow(
            key = row.key,
            label = row.label,
            subtitle = row.subtitle,
            currentValue = row.currentValue,
            keyboardType = KeyboardType.Text,
            onSaveString = onSaveString,
        )
        }

        is SettingRowUIModel.IntRow -> {
            TextSettingRow(
            key = row.key,
            label = row.label,
            subtitle = row.subtitle,
            currentValue = row.currentValue,
            keyboardType = KeyboardType.Number,
            onSaveString = onSaveString,
        )
        }

        is SettingRowUIModel.LongRow -> {
            TextSettingRow(
            key = row.key,
            label = row.label,
            subtitle = row.subtitle,
            currentValue = row.currentValue,
            keyboardType = KeyboardType.Number,
            onSaveString = onSaveString,
        )
        }
    }
}

@Composable
private fun BooleanSettingRow(
    row: SettingRowUIModel.BooleanRow,
    onSave: (SettingKey<*>, Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = row.label, style = MaterialTheme.typography.bodyLarge)
            if (row.subtitle != null) {
                Text(
                    text = row.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = row.currentValue,
            onCheckedChange = { onSave(row.key, it) },
        )
    }
}

@Composable
private fun TextSettingRow(
    key: SettingKey<*>,
    label: String,
    subtitle: String?,
    currentValue: String,
    keyboardType: KeyboardType,
    onSaveString: (SettingKey<*>, String) -> Unit,
) {
    var text by remember(currentValue) { mutableStateOf(currentValue) }
    var hasFocus by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier =
                Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (hasFocus && !focusState.isFocused) {
                        onSaveString(key, text)
                    }
                    hasFocus = focusState.isFocused
                },
                singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
    }
}
