package com.cramsan.edifikana.client.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

@Composable
@Preview
private fun EdifikanaTopBarPreview() = AppTheme {
    EdifikanaTopBar(
        title = "Title",
        onCloseClicked = {},
    )
}
