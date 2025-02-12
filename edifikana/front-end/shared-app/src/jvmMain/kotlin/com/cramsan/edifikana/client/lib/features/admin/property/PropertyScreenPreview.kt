package com.cramsan.edifikana.client.lib.features.admin.property

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Preview for the Property feature screen.
 */
@Preview
@Composable
private fun PropertyScreenPreview() {
    PropertyContent(
        content = PropertyUIModel(
            PropertyId(""),
            "Property 1",
        ),
        loading = false,
    )
}
