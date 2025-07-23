package com.cramsan.framework.sample.shared.features.main.logging

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Logging feature screen.
 */
@Preview
@Composable
private fun LoggingScreenPreview() {
    LoggingContent(
        content = LoggingUIState(true),
        onLogInfoSelected = {},
        onLogWarningSelected = {},
        onLogErrorSelected = {},
        onVerboseSelected = {},
        onDebugSelected = {},
    )
}
