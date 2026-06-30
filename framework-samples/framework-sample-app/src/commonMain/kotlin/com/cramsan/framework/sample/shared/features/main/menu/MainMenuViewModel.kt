package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.features.main.MainDestination
import com.cramsan.framework.sample.shared.features.main.welcome.ThemeSelection
import kotlinx.coroutines.launch

/**
 * Main Menu ViewModel.
 */
@FrontendViewModel
class MainMenuViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<MainMenuEvent, MainMenuUIState>(dependencies, MainMenuUIState.Initial, TAG) {
    /**
     * Navigate to the HaltUtil screen.
     */
    fun navigateToHaltUtil() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.HaltUtilDestination))
        }
    }

    /**
     * Navigate to the Logging screen.
     */
    fun navigateToLogging() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.LoggingDestination))
        }
    }

    /**
     * Navigate to the Preferences screen.
     */
    fun navigateToPreferences() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.PreferencesDestination))
        }
    }

    /**
     * Navigate to the ThreadUtil screen.
     */
    fun navigateToThreadUtil() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.ThreadUtilDestination))
        }
    }

    /**
     * Navigate to the AssertUtil screen.
     */
    fun navigateToAssertUtil() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.AssertUtilDestination))
        }
    }

    /**
     * Navigate to the Metrics screen.
     */
    fun navigateToMetrics() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.MetricsDestination))
        }
    }

    /**
     * Navigate to the Configuration screen.
     */
    fun navigateToConfiguration() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.ConfigurationDestination))
        }
    }

    /**
     * Navigate to the CrashHandler screen.
     */
    fun navigateToCrashHandler() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.CrashHandlerDestination))
        }
    }

    /**
     * Navigate to the UserEvents screen.
     */
    fun navigateToUserEvents() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.UserEventsDestination))
        }
    }

    /**
     * Navigate to the RemoteConfig screen.
     */
    fun navigateToRemoteConfig() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.RemoteConfigDestination))
        }
    }

    /**
     * Navigate to the DispatcherProvider screen.
     */
    fun navigateToDispatcher() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.DispatcherDestination))
        }
    }

    /**
     * Navigate to the welcome dialog to pick a theme.
     */
    fun navigateToWelcomeDialog() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateToScreen(MainDestination.WelcomeDialogDestination))
        }
    }

    /**
     * Called when the welcome dialog returns a [ThemeSelection] result.
     */
    fun onThemeSelected(theme: ThemeSelection) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(selectedTheme = theme) }
        }
    }

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}
