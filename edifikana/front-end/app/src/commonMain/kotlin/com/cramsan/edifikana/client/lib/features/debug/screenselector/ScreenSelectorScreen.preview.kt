package com.cramsan.edifikana.client.lib.features.debug.screenselector

import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the ScreenSelector feature screen.
 */
@ScreenPreviews
@Composable
private fun ScreenSelectorScreenPreview() {
    ScreenSelectorContent(
        content =
        ScreenSelectorUIState(
            title = "ScreenSelectorScreenPreview",
            isLoading = true,
        ),
        onBackSelected = {},
        onScreenSelected = { },
    )
}
