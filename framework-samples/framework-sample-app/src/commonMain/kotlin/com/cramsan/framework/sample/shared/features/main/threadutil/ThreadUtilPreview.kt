package com.cramsan.framework.sample.shared.features.main.threadutil

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
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
