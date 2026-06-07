package com.cramsan.edifikana.client.lib.features.home.appshell

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews

@DevicePreviews
@Composable
private fun AppShellTasksPreview() {
    AppTheme {
        AppShellContent(
            uiState = AppShellUIState(selectedTab = AppShellTab.Tasks),
            onTabSelected = {},
            onAccountSelected = {},
            onNotificationsSelected = {},
            onSettingsSelected = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AppShellMobileMorePreview() {
    AppTheme {
        AppShellContent(
            uiState = AppShellUIState(selectedTab = AppShellTab.More),
            onTabSelected = {},
            onAccountSelected = {},
            onNotificationsSelected = {},
            onSettingsSelected = {},
        )
    }
}
