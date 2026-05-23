package com.cramsan.framework.sample.shared.features.main.threadutil

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ThreadUtilScreenPreview() {
    ThreadUtilContent(
        uiState =
        ThreadUtilUIState(
            isUIThread = true,
            isBackgroundThread = false,
            lastAction = "isUIThread() → true",
        ),
        onCheckIsUIThread = {},
        onCheckIsBackgroundThread = {},
        onAssertIsUIThread = {},
        onAssertIsBackgroundThread = {},
        onBack = {},
    )
}
