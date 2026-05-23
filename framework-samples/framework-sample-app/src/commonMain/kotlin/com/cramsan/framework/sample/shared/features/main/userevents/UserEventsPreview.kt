package com.cramsan.framework.sample.shared.features.main.userevents

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
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
