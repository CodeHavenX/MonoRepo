package com.cramsan.framework.sample.shared.features.main.crashhandler

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun CrashHandlerScreenPreview() {
    CrashHandlerContent(
        uiState = CrashHandlerUIState(isInitialized = true),
        onInitialize = {},
        onBack = {},
    )
}
