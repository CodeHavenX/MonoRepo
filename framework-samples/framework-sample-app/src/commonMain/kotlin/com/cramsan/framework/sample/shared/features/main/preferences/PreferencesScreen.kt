package com.cramsan.framework.sample.shared.features.main.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * Preferences screen.
 *
 * Demonstrates all [com.cramsan.framework.preferences.Preferences] API methods.
 */
@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PreferencesEvent.Noop -> Unit
        }
    }

    PreferencesContent(
        uiState = uiState,
        onSaveString = { viewModel.saveString() },
        onLoadString = { viewModel.loadString() },
        onSaveInt = { viewModel.saveInt() },
        onLoadInt = { viewModel.loadInt() },
        onSaveLong = { viewModel.saveLong() },
        onLoadLong = { viewModel.loadLong() },
        onSaveBoolean = { viewModel.saveBoolean() },
        onLoadBoolean = { viewModel.loadBoolean() },
        onRemove = { viewModel.remove() },
        onClear = { viewModel.clear() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the Preferences screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferencesContent(
    uiState: PreferencesUIState,
    onSaveString: () -> Unit,
    onLoadString: () -> Unit,
    onSaveInt: () -> Unit,
    onLoadInt: () -> Unit,
    onSaveLong: () -> Unit,
    onLoadLong: () -> Unit,
    onSaveBoolean: () -> Unit,
    onLoadBoolean: () -> Unit,
    onRemove: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                Text("String value: ${uiState.stringValue ?: "(not loaded)"}")
                Text("Int value: ${uiState.intValue ?: "(not loaded)"}")
                Text("Long value: ${uiState.longValue ?: "(not loaded)"}")
                Text("Boolean value: ${uiState.booleanValue ?: "(not loaded)"}")
                Button(onClick = onSaveString, modifier = modifier) { Text("Save String (\"hello\")") }
                Button(onClick = onLoadString, modifier = modifier) { Text("Load String") }
                Button(onClick = onSaveInt, modifier = modifier) { Text("Save Int (42)") }
                Button(onClick = onLoadInt, modifier = modifier) { Text("Load Int") }
                Button(onClick = onSaveLong, modifier = modifier) { Text("Save Long (100)") }
                Button(onClick = onLoadLong, modifier = modifier) { Text("Load Long") }
                Button(onClick = onSaveBoolean, modifier = modifier) { Text("Save Boolean (true)") }
                Button(onClick = onLoadBoolean, modifier = modifier) { Text("Load Boolean") }
                Button(onClick = onRemove, modifier = modifier) { Text("Remove String Key") }
                Button(onClick = onClear, modifier = modifier) { Text("Clear All") }
            },
        )
    }
}
