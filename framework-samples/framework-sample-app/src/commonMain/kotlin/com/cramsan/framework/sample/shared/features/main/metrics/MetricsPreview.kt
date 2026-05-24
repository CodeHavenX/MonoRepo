package com.cramsan.framework.sample.shared.features.main.metrics

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
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
