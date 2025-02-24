package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the Validation feature screen.
 */
@Preview
@Composable
private fun ValidationScreenPreview() {
    ValidationContent(
        content = ValidationUIState(true),
    )
}
