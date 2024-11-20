package com.cramsan.edifikana.client.lib.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Composable
@Preview
private fun EdifikanaTopBarPreview() {
    EdifikanaTopBar(
        title = "Title",
        showUpArrow = true,
        onUpArrowClicked = {},
        onAccountClicked = {},
    )
}
