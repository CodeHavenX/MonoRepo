package com.cramsan.runasimi.client.lib.features.main.menu

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Menu feature screen.
 */
@Preview
@Composable
private fun MenuScreenPreview() {
    AppTheme {
        MenuContent(
            content = MenuUIState(
                title = "MenuScreenPreview",
                isLoading = true,
            ),
            onBackSelected = {},
        )
    }
}
