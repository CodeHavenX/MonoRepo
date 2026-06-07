package com.cramsan.framework.sample.shared.features.main.preferences

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

@ScreenPreviews
@Composable
private fun PreferencesScreenPreview() {
    PreferencesContent(
        uiState =
        PreferencesUIState(
            stringValue = "hello",
            intValue = 42,
            longValue = 100L,
            booleanValue = true,
        ),
        onSaveString = {},
        onLoadString = {},
        onSaveInt = {},
        onLoadInt = {},
        onSaveLong = {},
        onLoadLong = {},
        onSaveBoolean = {},
        onLoadBoolean = {},
        onRemove = {},
        onClear = {},
        onBack = {},
    )
}
