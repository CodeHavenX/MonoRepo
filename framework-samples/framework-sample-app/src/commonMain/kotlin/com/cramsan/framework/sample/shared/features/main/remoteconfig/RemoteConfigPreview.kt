package com.cramsan.framework.sample.shared.features.main.remoteconfig

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
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
