package com.cramsan.edifikana.client.lib.features.root.admin.properties

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
        content = PropertyManagerUIModel(listOf(
            PropertyUIModel(PropertyId(""), "Property 1", "Address 1"),
            PropertyUIModel(PropertyId(""), "Property 2", "Address 2"),
            PropertyUIModel(PropertyId(""), "Property 3", "Address 3"),
        )),
        loading = false,
        onPropertyClicked = {},
        onAddPropertyClicked = {},
    )
}