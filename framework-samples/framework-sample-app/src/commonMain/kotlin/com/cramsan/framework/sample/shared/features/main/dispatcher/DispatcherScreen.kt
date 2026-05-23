package com.cramsan.framework.sample.shared.features.main.dispatcher

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
 * DispatcherProvider screen.
 *
 * Demonstrates the [com.cramsan.framework.core.DispatcherProvider] API by displaying
 * the string representation of each returned dispatcher.
 */
@Composable
fun DispatcherScreen(
    viewModel: DispatcherViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            DispatcherEvent.Noop -> Unit
        }
    }

    DispatcherContent(
        uiState = uiState,
        onQueryIoDispatcher = { viewModel.queryIoDispatcher() },
        onQueryUiDispatcher = { viewModel.queryUiDispatcher() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the DispatcherProvider screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DispatcherContent(
    uiState: DispatcherUIState,
    onQueryIoDispatcher: () -> Unit,
    onQueryUiDispatcher: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispatcher Provider") },
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
                Text("IO Dispatcher: ${uiState.ioDispatcherInfo}")
                Text("UI Dispatcher: ${uiState.uiDispatcherInfo}")
                Button(onClick = onQueryIoDispatcher, modifier = modifier) { Text("ioDispatcher()") }
                Button(onClick = onQueryUiDispatcher, modifier = modifier) { Text("uiDispatcher()") }
            },
        )
    }
}
