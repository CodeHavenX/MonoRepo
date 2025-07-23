package com.cramsan.edifikana.client.lib.features.debug.screenselector

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the ScreenSelector feature screen.
 */
@Preview
@Composable
private fun ScreenSelectorScreenPreview() {
    ScreenSelectorContent(
        content = ScreenSelectorUIState(
            title = "ScreenSelectorScreenPreview",
            isLoading = true,
        ),
        onBackSelected = {},
        onScreenSelected = { },
    )
}
