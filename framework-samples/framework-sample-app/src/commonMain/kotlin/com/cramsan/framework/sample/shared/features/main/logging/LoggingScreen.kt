package com.cramsan.framework.sample.shared.features.main.logging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * Logging screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun LoggingScreen(viewModel: LoggingViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // For other possible lifecycle events, see the Lifecycle.Event documentation.
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            LoggingEvent.Noop -> Unit
        }
    }

    // Render the screen
    LoggingContent(
        uiState,
        onLogInfoSelected = { viewModel.logInfo() },
        onLogWarningSelected = { viewModel.logWarning() },
        onLogErrorSelected = { viewModel.logError() },
        onVerboseSelected = { viewModel.logVerbose() },
        onDebugSelected = { viewModel.logDebug() },
    )
}

/**
 * Content of the Logging screen.
 */
@Composable
internal fun LoggingContent(
    content: LoggingUIState,
    onVerboseSelected: () -> Unit,
    onDebugSelected: () -> Unit,
    onLogInfoSelected: () -> Unit,
    onLogWarningSelected: () -> Unit,
    onLogErrorSelected: () -> Unit,
) {
    Scaffold { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                Button(
                    onClick = onVerboseSelected,
                    modifier = modifier,
                ) {
                    Text("Log Verbose")
                }
                Button(
                    onClick = onDebugSelected,
                    modifier = modifier,
                ) {
                    Text("Log Debug")
                }
                Button(
                    onClick = onLogInfoSelected,
                    modifier = modifier,
                ) {
                    Text("Log Info")
                }
                Button(
                    onClick = onLogWarningSelected,
                    modifier = modifier,
                ) {
                    Text("Log Warning")
                }
                Button(
                    onClick = onLogErrorSelected,
                    modifier = modifier,
                ) {
                    Text("Log Error")
                }
            },
            overlay = {
                LoadingAnimationOverlay(isLoading = content.isLoading)
            },
        )
    }
}
