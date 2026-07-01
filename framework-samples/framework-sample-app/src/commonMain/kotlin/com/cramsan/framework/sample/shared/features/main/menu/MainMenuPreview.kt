package com.cramsan.framework.sample.shared.features.main.menu

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun MainMenuScreenPreview() {
    MainMenuContent(
        selectedTheme = null,
        onHaltUtilSelected = {},
        onLoggingSelected = {},
        onPreferencesSelected = {},
        onThreadUtilSelected = {},
        onAssertUtilSelected = {},
        onMetricsSelected = {},
        onConfigurationSelected = {},
        onCrashHandlerSelected = {},
        onUserEventsSelected = {},
        onRemoteConfigSelected = {},
        onDispatcherSelected = {},
        onWelcomeSelected = {},
    )
}
