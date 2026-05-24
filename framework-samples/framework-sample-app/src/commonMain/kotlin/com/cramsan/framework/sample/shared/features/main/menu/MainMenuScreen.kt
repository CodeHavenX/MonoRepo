package com.cramsan.framework.sample.shared.features.main.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main Menu screen.
 *
 * Lists all available framework API sample screens.
 */
@Composable
fun MainMenuScreen(
    viewModel: MainMenuViewModel = koinViewModel(),
) {
    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MainMenuEvent.Noop -> Unit
        }
    }

    MainMenuContent(
        onHaltUtilSelected = { viewModel.navigateToHaltUtil() },
        onLoggingSelected = { viewModel.navigateToLogging() },
        onPreferencesSelected = { viewModel.navigateToPreferences() },
        onThreadUtilSelected = { viewModel.navigateToThreadUtil() },
        onAssertUtilSelected = { viewModel.navigateToAssertUtil() },
        onMetricsSelected = { viewModel.navigateToMetrics() },
        onConfigurationSelected = { viewModel.navigateToConfiguration() },
        onCrashHandlerSelected = { viewModel.navigateToCrashHandler() },
        onUserEventsSelected = { viewModel.navigateToUserEvents() },
        onRemoteConfigSelected = { viewModel.navigateToRemoteConfig() },
        onDispatcherSelected = { viewModel.navigateToDispatcher() },
    )
}

/**
 * Content of the Main Menu screen.
 */
@Composable
internal fun MainMenuContent(
    onHaltUtilSelected: () -> Unit,
    onLoggingSelected: () -> Unit,
    onPreferencesSelected: () -> Unit,
    onThreadUtilSelected: () -> Unit,
    onAssertUtilSelected: () -> Unit,
    onMetricsSelected: () -> Unit,
    onConfigurationSelected: () -> Unit,
    onCrashHandlerSelected: () -> Unit,
    onUserEventsSelected: () -> Unit,
    onRemoteConfigSelected: () -> Unit,
    onDispatcherSelected: () -> Unit,
) {
    Scaffold { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            sectionContent = { modifier ->
                Button(onClick = onLoggingSelected, modifier = modifier) { Text("Logging") }
                Button(onClick = onHaltUtilSelected, modifier = modifier) { Text("Halt Util") }
                Button(onClick = onPreferencesSelected, modifier = modifier) { Text("Preferences") }
                Button(onClick = onThreadUtilSelected, modifier = modifier) { Text("Thread Util") }
                Button(onClick = onAssertUtilSelected, modifier = modifier) { Text("Assert Util") }
                Button(onClick = onMetricsSelected, modifier = modifier) { Text("Metrics") }
                Button(onClick = onConfigurationSelected, modifier = modifier) { Text("Configuration") }
                Button(onClick = onCrashHandlerSelected, modifier = modifier) { Text("Crash Handler") }
                Button(onClick = onUserEventsSelected, modifier = modifier) { Text("User Events") }
                Button(onClick = onRemoteConfigSelected, modifier = modifier) { Text("Remote Config") }
                Button(onClick = onDispatcherSelected, modifier = modifier) { Text("Dispatcher Provider") }
            },
        )
    }
}
