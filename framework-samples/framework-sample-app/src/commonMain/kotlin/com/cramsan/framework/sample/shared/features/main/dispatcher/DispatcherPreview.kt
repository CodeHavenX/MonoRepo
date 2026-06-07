package com.cramsan.framework.sample.shared.features.main.dispatcher

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
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
