package com.cramsan.framework.sample.shared.features.main.crashhandler

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
 * CrashHandler screen.
 *
 * Demonstrates the [com.cramsan.framework.crashhandler.CrashHandler] API.
 */
@Composable
fun CrashHandlerScreen(
    viewModel: CrashHandlerViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            CrashHandlerEvent.Noop -> Unit
        }
    }

    CrashHandlerContent(
        uiState = uiState,
        onInitialize = { viewModel.initialize() },
        onBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the CrashHandler screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CrashHandlerContent(
    uiState: CrashHandlerUIState,
    onInitialize: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crash Handler") },
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
                Text(if (uiState.isInitialized) "Status: initialized" else "Status: not initialized")
                Button(onClick = onInitialize, modifier = modifier) { Text("initialize()") }
            },
        )
    }
}
