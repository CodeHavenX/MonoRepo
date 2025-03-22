package com.cramsan.framework.sample.shared.features.main.halt

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

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
