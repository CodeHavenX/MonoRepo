package com.cramsan.framework.sample.shared.features.main.userevents

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun UserEventsScreenPreview() {
    UserEventsContent(
        uiState = UserEventsUIState(lastAction = "log(tag, event) called"),
        onInitialize = {},
        onLogEvent = {},
        onLogEventWithMetadata = {},
        onBack = {},
    )
}
