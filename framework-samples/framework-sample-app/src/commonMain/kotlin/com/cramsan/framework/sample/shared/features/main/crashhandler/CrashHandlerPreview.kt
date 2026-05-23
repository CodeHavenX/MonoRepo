package com.cramsan.framework.sample.shared.features.main.crashhandler

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun CrashHandlerScreenPreview() {
    CrashHandlerContent(
        uiState = CrashHandlerUIState(isInitialized = true),
        onInitialize = {},
        onBack = {},
    )
}
