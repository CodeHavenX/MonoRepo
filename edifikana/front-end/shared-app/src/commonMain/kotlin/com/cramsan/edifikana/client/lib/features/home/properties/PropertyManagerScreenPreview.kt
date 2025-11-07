package com.cramsan.edifikana.client.lib.features.home.properties

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.PropertyId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the PropertyManager feature screen.
 */
@Preview
@Composable
private fun PropertyManagerScreenPreview() = AppTheme {
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
    )
}
