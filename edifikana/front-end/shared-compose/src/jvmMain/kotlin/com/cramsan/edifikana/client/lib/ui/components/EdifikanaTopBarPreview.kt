package com.cramsan.edifikana.client.lib.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme

@Composable
@Preview
private fun EdifikanaTopBarPreview() = AppTheme {
    EdifikanaTopBar(
        title = "Title",
        showUpArrow = true,
        onUpArrowClicked = {},
        onAccountClicked = {},
    )
}
