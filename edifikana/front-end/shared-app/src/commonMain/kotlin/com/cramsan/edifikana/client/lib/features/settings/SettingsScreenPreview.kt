package com.cramsan.edifikana.client.lib.features.settings

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
