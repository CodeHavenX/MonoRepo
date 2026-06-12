package com.cramsan.runasimi.client.lib.features.main.yupay

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.lib.manager.Content
import com.cramsan.runasimi.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Yupay feature screen.
 */
@ScreenPreviews
@Composable
private fun YupayScreenPreview() {
    AppTheme {
        YupayContent(
            content =
            YupayUIState(
                Content(
                    translated = "Iskay chunka pichqayuq",
                    original = "25",
                ),
            ),
        )
    }
}
