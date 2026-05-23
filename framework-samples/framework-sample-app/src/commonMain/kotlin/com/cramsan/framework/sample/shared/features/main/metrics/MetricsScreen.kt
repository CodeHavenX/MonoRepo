package com.cramsan.framework.sample.shared.features.main.metrics

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
 * Metrics screen.
 *
 * Demonstrates all [com.cramsan.framework.metrics.MetricsInterface] API methods.
 */
@Composable
fun MetricsScreen(
    viewModel: MetricsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MetricsEvent.Noop -> Unit
        }
    }

    MetricsContent(
        uiState = uiState,
        onInitialize = { viewModel.initialize() },
        onRecordCount = { viewModel.recordCount() },
        onRecordLatency = { viewModel.recordLatency() },
        onRecordEvent = { viewModel.recordEvent() },
        onRecordSuccess = { viewModel.recordSuccess() },
        onRecordFailure = { viewModel.recordFailure() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the Metrics screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MetricsContent(
    uiState: MetricsUIState,
    onInitialize: () -> Unit,
    onRecordCount: () -> Unit,
    onRecordLatency: () -> Unit,
    onRecordEvent: () -> Unit,
    onRecordSuccess: () -> Unit,
    onRecordFailure: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metrics") },
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
                Text("Last action: ${uiState.lastAction}")
                Button(onClick = onInitialize, modifier = modifier) { Text("initialize()") }
                Button(onClick = onRecordCount, modifier = modifier) { Text("record(COUNT)") }
                Button(onClick = onRecordLatency, modifier = modifier) { Text("record(LATENCY)") }
                Button(onClick = onRecordEvent, modifier = modifier) { Text("record(EVENT)") }
                Button(onClick = onRecordSuccess, modifier = modifier) { Text("record(SUCCESS)") }
                Button(onClick = onRecordFailure, modifier = modifier) { Text("record(FAILURE)") }
            },
        )
    }
}
