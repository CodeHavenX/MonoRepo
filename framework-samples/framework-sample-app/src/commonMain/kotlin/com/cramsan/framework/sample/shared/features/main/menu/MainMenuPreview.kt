package com.cramsan.framework.sample.shared.features.main.menu

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun MainMenuScreenPreview() {
    MainMenuContent(
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
    )
}
