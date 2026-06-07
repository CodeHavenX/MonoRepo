package com.cramsan.framework.sample.shared.features.main.logging

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Logging feature screen.
 */
@ScreenPreviews
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
