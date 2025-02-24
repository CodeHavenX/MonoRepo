package com.cramsan.framework.sample.shared.features.main.halt

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.sample.shared.features.ApplicationViewModel
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * HaltUtil screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun HaltUtilScreen(
    viewModel: HaltUtilViewModel = koinViewModel(),
    applicationViewModel: ApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(HaltUtilEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            HaltUtilEvent.Noop -> Unit
            is HaltUtilEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    HaltUtilContent(
        uiState,
        onStopThreadSelected = { viewModel.stopThread() },
        onResumeThreadSelected = { viewModel.resumeThread() },
        onCrashAppSelected = { viewModel.crashApp() },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun HaltUtilContent(
    content: HaltUtilUIState,
    onStopThreadSelected: () -> Unit,
    onResumeThreadSelected: () -> Unit,
    onCrashAppSelected: () -> Unit,
) {
    Scaffold { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding),
            sectionContent = { modifier ->
                Button(
                    onClick = onStopThreadSelected,
                    modifier = modifier,
                ) {
                    Text("Stop Thread")
                }
                Button(
                    onClick = onResumeThreadSelected,
                    modifier = modifier,
                ) {
                    Text("Resume Thread")
                }
                Button(
                    onClick = onCrashAppSelected,
                    modifier = modifier,
                ) {
                    Text("Crash App")
                }
            },
        )
        LoadingAnimationOverlay(isLoading = content.isLoading)
    }
}
