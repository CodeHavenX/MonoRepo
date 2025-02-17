package com.cramsan.edifikana.client.lib.features.admin.properties

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Preview for the PropertyManager feature screen.
 */
@Preview
@Composable
private fun PropertyManagerScreenPreview() {
    PropertyManagerContent(
        content = PropertyManagerUIState(
            content = PropertyManagerUIModel(
                listOf(
                    PropertyUIModel(PropertyId(""), "Sunset Villa", "123 Sunset Blvd"),
                    PropertyUIModel(PropertyId(""), "Ocean Breeze", "456 Ocean Ave"),
                    PropertyUIModel(PropertyId(""), "Mountain Retreat", "789 Mountain Rd"),
                )
            ),
            isLoading = false,
        ),
        onPropertyClicked = {},
        onAddPropertyClicked = {},
        onBackSelected = {},
    )
}
