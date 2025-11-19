package com.cramsan.runasimi.client.lib.features.main.questions

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.lib.manager.Content
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Questions feature screen.
 */
@Preview
@Composable
private fun QuestionsScreenPreview() {
    AppTheme {
        QuestionsContent(
            content = QuestionsUIState(
                Content(
                    translated = "Chaymi rikhurqa",
                    original = "Sample",
                )
            ),
        )
    }
}
