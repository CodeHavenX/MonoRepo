package com.cramsan.edifikana.client.lib.features.settings.general

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.components.themetoggle.SelectedTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SettingsScreenPreview() = AppTheme {
    SettingsContent(
        uiState = SettingsUIState(
            selectedTheme = SelectedTheme.LIGHT,
        ),
        onThemeSelected = {},
        onBackSelected = { },
    )
}

@Preview(locale = "es")
@Composable
private fun SettingsScreenPreview_ES() = AppTheme {
    SettingsContent(
        uiState = SettingsUIState(
            selectedTheme = SelectedTheme.LIGHT,
        ),
        onThemeSelected = {},
        onBackSelected = { },
    )
}
