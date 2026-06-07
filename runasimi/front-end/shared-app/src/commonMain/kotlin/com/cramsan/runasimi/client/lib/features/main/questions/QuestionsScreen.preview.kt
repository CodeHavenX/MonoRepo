package com.cramsan.runasimi.client.lib.features.main.questions

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.lib.manager.Content
import com.cramsan.runasimi.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Questions feature screen.
 */
@ScreenPreviews
@Composable
private fun QuestionsScreenPreview() {
    AppTheme {
        QuestionsContent(
            content =
            QuestionsUIState(
                Content(
                    translated = "Sample",
                    original = "Chaymi rikhurqa",
                ),
            ),
        )
    }
}
