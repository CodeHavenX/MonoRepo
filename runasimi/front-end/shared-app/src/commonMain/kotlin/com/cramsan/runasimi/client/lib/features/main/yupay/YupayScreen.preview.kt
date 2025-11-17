package com.cramsan.runasimi.client.lib.features.main.yupay

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.lib.manager.Content
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Yupay feature screen.
 */
@Preview
@Composable
private fun YupayScreenPreview() {
    AppTheme {
        YupayContent(
            content = YupayUIState(
                Content(
                    translated = "Iskay chunka pichqayuq",
                    original = "25",
                )
            )
        )
    }
}
