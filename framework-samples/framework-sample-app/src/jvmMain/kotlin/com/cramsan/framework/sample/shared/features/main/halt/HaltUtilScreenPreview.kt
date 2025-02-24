package com.cramsan.framework.sample.shared.features.main.halt

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the HaltUtil feature screen.
 */
@Preview
@Composable
private fun HaltUtilScreenPreview() {
    HaltUtilContent(
        content = HaltUtilUIState(true),
        onCrashAppSelected = {},
        onStopThreadSelected = {},
        onResumeThreadSelected = {},
    )
}
