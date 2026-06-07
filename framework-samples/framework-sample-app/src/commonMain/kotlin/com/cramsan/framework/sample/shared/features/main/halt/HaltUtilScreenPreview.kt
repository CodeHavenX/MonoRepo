package com.cramsan.framework.sample.shared.features.main.halt

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the HaltUtil feature screen.
 */
@ScreenPreviews
@Composable
private fun HaltUtilScreenPreview() {
    HaltUtilContent(
        content = HaltUtilUIState(true),
        onCrashAppSelected = {},
        onStopThreadSelected = {},
        onResumeThreadSelected = {},
    )
}
