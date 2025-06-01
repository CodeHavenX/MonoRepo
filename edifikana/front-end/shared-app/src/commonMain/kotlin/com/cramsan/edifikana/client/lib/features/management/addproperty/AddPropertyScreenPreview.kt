package com.cramsan.edifikana.client.lib.features.management.addproperty

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddProperty feature screen.
 */
@Preview
@Composable
private fun AddPropertyScreenPreview() = AppTheme {
    AddPropertyContent(
        content = AddPropertyUIState(true),
        onBackSelected = {},
        onAddPropertySelected = { _, _ -> }
    )
}
