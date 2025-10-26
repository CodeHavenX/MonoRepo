package com.cramsan.runasimi.client.lib.features.main.verbs

import androidx.compose.runtime.Composable
import com.cramsan.runasimi.client.lib.manager.Content
import com.cramsan.runasimi.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Verbs feature screen.
 */
@Preview
@Composable
private fun VerbsScreenPreview() {
    AppTheme {
        VerbsContent(
            content = VerbsUIState(
                Content(
                    translated = "Iskay chunka pichqayuq",
                    original = "25",
                )
            ),
        )
    }
}
