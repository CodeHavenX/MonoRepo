package com.cramsan.framework.sample.shared.features.main.remoteconfig

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun RemoteConfigScreenPreview() {
    RemoteConfigContent(
        uiState =
        RemoteConfigUIState(
            isLoading = false,
            isPayloadReady = true,
            lastAction = "downloadConfigPayload() → success=true",
            payloadInfo = "featureEnabled=true, configValue=sample-value",
        ),
        onCheckIsReady = {},
        onDownloadPayload = {},
        onDownloadAsync = {},
        onGetOrNull = {},
        onGetOrDefault = {},
        onBack = {},
    )
}
