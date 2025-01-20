package com.cramsan.edifikana.client.lib.features.root.debug.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the Debug feature screen.
 */
@Preview
@Composable
private fun DebugScreenPreview() {
    DebugContent(
        content = DebugUIModel(
            listOf(
                Field.BooleanField(
                    key = "debug_key",
                    value = true,
                ),
                Field.StringField(
                    key = "string_key",
                    value = "string_value",
                ),
            )
        ),
        onFieldValueChanged = { _, _ -> }
    )
}
