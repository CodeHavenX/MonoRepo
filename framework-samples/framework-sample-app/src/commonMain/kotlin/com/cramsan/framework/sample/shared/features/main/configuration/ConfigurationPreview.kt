package com.cramsan.framework.sample.shared.features.main.configuration

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun ConfigurationScreenPreview() {
    ConfigurationContent(
        uiState =
        ConfigurationUIState.Read(
            stringValue = null,
            intValue = null,
            longValue = null,
            booleanValue = null,
        ),
        onReadString = {},
        onReadInt = {},
        onReadLong = {},
        onReadBoolean = {},
        onBack = {},
    )
}
