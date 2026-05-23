package com.cramsan.framework.sample.shared.features.main.dispatcher

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun DispatcherScreenPreview() {
    DispatcherContent(
        uiState =
        DispatcherUIState(
            ioDispatcherInfo = "Dispatchers.IO",
            uiDispatcherInfo = "Dispatchers.Main.immediate",
        ),
        onQueryIoDispatcher = {},
        onQueryUiDispatcher = {},
        onBack = {},
    )
}
