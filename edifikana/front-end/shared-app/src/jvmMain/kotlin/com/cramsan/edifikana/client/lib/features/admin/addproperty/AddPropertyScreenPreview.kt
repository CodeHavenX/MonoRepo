package com.cramsan.edifikana.client.lib.features.admin.addproperty

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

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
