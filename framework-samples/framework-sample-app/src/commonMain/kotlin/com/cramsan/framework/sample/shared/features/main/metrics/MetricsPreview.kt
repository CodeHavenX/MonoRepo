package com.cramsan.framework.sample.shared.features.main.metrics

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun MetricsScreenPreview() {
    MetricsContent(
        uiState = MetricsUIState(lastAction = "record(COUNT) called"),
        onInitialize = {},
        onRecordCount = {},
        onRecordLatency = {},
        onRecordEvent = {},
        onRecordSuccess = {},
        onRecordFailure = {},
        onBack = {},
    )
}
