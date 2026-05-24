package com.cramsan.framework.sample.shared.features.main.configuration

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
 * Configuration screen.
 *
 * Demonstrates all [com.cramsan.framework.configuration.Configuration] API methods.
 * The no-op implementation always returns null for all keys.
 */
@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ConfigurationEvent.Noop -> Unit
        }
    }

    ConfigurationContent(
        uiState = uiState,
        onReadString = { viewModel.readString() },
        onReadInt = { viewModel.readInt() },
        onReadLong = { viewModel.readLong() },
        onReadBoolean = { viewModel.readBoolean() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the Configuration screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConfigurationContent(
    uiState: ConfigurationUIState,
    onReadString: () -> Unit,
    onReadInt: () -> Unit,
    onReadLong: () -> Unit,
    onReadBoolean: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuration") },
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
                when (uiState) {
                    is ConfigurationUIState.NotRead ->
                        Text("No values read yet (no-op returns null for all keys)")
                    is ConfigurationUIState.Read -> {
                        Text("String: ${uiState.stringValue ?: "null"}")
                        Text("Int: ${uiState.intValue ?: "null"}")
                        Text("Long: ${uiState.longValue ?: "null"}")
                        Text("Boolean: ${uiState.booleanValue ?: "null"}")
                    }
                }
                Button(onClick = onReadString, modifier = modifier) { Text("readString(\"sample_config_key\")") }
                Button(onClick = onReadInt, modifier = modifier) { Text("readInt(\"sample_config_key\")") }
                Button(onClick = onReadLong, modifier = modifier) { Text("readLong(\"sample_config_key\")") }
                Button(onClick = onReadBoolean, modifier = modifier) { Text("readBoolean(\"sample_config_key\")") }
            },
        )
    }
}
